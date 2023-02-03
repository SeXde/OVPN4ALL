package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.BandwidthDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.TestUtils.testException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatusServiceTest {

    @Mock
    private CommandService commandService;
    @Mock
    private ConfigService configService;

    private StatusService statusService;

    @BeforeEach
    void setup() {
        statusService = new StatusServiceImpl(commandService, configService);
    }

    @Test
    @DisplayName("Test isActive when vpn on")
    void isActiveTest_on() throws IOException, InterruptedException {
        when(commandService.isActive()).thenReturn(true);
        assertTrue(statusService.isActive());
        verify(commandService, times(1)).isActive();
        verifyNoInteractions(configService);
    }

    @Test
    @DisplayName("Test isActive when vpn off")
    void isActiveTest_off() throws IOException, InterruptedException {
        when(commandService.isActive()).thenReturn(false);
        assertFalse(statusService.isActive());
        verify(commandService, times(1)).isActive();
        verifyNoInteractions(configService);
    }

    @Test
    @DisplayName("Test isActive when got exception executing command")
    void isActiveTest_exception() throws IOException, InterruptedException {
        when(commandService.isActive()).thenThrow(RuntimeException.class);
        testException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot fetch vpn status", () -> statusService.isActive());
        verify(commandService, times(1)).isActive();
        verifyNoInteractions(configService);
    }

    @Test
    @DisplayName("Test turnOn when config is present")
    void turnOnTest_present() throws IOException, InterruptedException {
        when(commandService.isActive()).thenReturn(false).thenReturn(false).thenReturn(true);
        assertTrue(statusService.turnOn());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(3)).shutdown();
        verify(commandService, times(3)).startUp();
        verify(commandService, times(3)).isActive();
    }

    @Test
    @DisplayName("Test turnOn when config is not present")
    void turnOnTest_not_present() throws IOException, InterruptedException {
        final String exMessage = "Config not found";
        when(configService.getConfig()).thenThrow(new CustomException(exMessage, HttpStatus.NOT_FOUND));
        testException(HttpStatus.NOT_FOUND, exMessage, () -> statusService.turnOn());
        verifyNoInteractions(commandService);
    }

    @Test
    @DisplayName("Test turnOn when shutdown throws exception")
    void turnOnTest_shutdownException() throws IOException, InterruptedException {
        final String exMessage = "Error shutdown";
        doThrow(new RuntimeException(exMessage)).when(commandService).shutdown();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, exMessage, () -> statusService.turnOn());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).shutdown();
        verify(commandService, never()).startUp();
        verify(commandService, never()).isActive();
    }

    @Test
    @DisplayName("Test turnOn when startUp throws exception")
    void turnOnTest_startUpException() throws IOException, InterruptedException {
        final String exMessage = "Error startUp";
        doThrow(new RuntimeException(exMessage)).when(commandService).startUp();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, exMessage, () -> statusService.turnOn());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).shutdown();
        verify(commandService, times(1)).startUp();
        verify(commandService, never()).isActive();
    }

    @Test
    @DisplayName("Test turnOn when isActive throws exception")
    void turnOnTest_isActiveException() throws IOException, InterruptedException {
        final String exMessage = "Error isActive";
        doThrow(new RuntimeException(exMessage)).when(commandService).isActive();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, exMessage, () -> statusService.turnOn());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).shutdown();
        verify(commandService, times(1)).startUp();
        verify(commandService, times(1)).isActive();
    }

    @Test
    @DisplayName("Test turnOff when config is present")
    void turnOffTest_present() throws IOException, InterruptedException {
         when(commandService.isActive()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
         statusService.turnOff();
         verify(configService, times(1)).getConfig();
         verify(commandService, times(4)).shutdown();
         verify(commandService, never()).startUp();
         verify(commandService, times(4)).isActive();
    }

    @Test
    @DisplayName("Test turnOff when config is not present")
    void turnOffTest_not_present() throws IOException, InterruptedException {
        final var status = HttpStatus.NOT_FOUND;
        final var msg = "Config not found";
        doThrow(new CustomException(msg, status)).when(configService).getConfig();
        testException(status, msg, () -> statusService.turnOff());
        verify(configService, times(1)).getConfig();
        verifyNoInteractions(commandService);
    }

    @Test
    @DisplayName("Test turnOff when shutdown exception")
    void turnOffTest_shutdown_exception() throws IOException, InterruptedException {
        final var msg = "cannot shutdown";
        doThrow(new RuntimeException(msg)).when(commandService).shutdown();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, msg, () -> statusService.turnOff());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).shutdown();
        verify(commandService, never()).isActive();
    }

    @Test
    @DisplayName("Test turnOff when isActive exception")
    void turnOffTest_isActive_exception() throws IOException, InterruptedException {
        final var msg = "cannot shutdown";
        doThrow(new RuntimeException(msg)).when(commandService).isActive();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, msg, () -> statusService.turnOff());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).shutdown();
        verify(commandService, times(1)).isActive();
    }

    @ParameterizedTest
    @MethodSource("generateThroughput")
    @DisplayName("Test getThroughput")
    void getThroughputTest(List<Object> params) throws IOException, ExecutionException, InterruptedException {
        final String[] throughput = (String[]) params.get(0);
        when(commandService.readThroughput()).thenReturn(throughput);
        final BandwidthDTO bandwidthDTO = (BandwidthDTO) params.get(1);
        final var result = statusService.getThroughput();
        assertEquals(bandwidthDTO.getIn(), result.getIn());
        assertEquals(bandwidthDTO.getOut(), result.getOut());
    }

    static Stream<List<Object>> generateThroughput() {
        return Stream.of(
            List.of(new String[]{}, new BandwidthDTO(0.0f, 0.0f)),
            List.of(new String[]{"22", "31"}, new BandwidthDTO(22.0f, 31.0f)),
            List.of(new String[]{"41", "11"}, new BandwidthDTO(41.0f, 11.0f)),
            List.of(new String[]{"22.56", "31.89"}, new BandwidthDTO(22.56f, 31.89f)),
            List.of(new String[]{"0", "31"}, new BandwidthDTO(0.0f, 31.0f)),
            List.of(new String[]{"22", "0"}, new BandwidthDTO(22.0f, 0.0f)),
            List.of(new String[]{"7777.34", "909887.67"}, new BandwidthDTO(7777.34f, 909887.67f)),
            List.of(new String[]{"2289", "31123"}, new BandwidthDTO(2289.0f, 31123.0f))
        );
    }

    @Test
    @DisplayName("Test getThroughput when readThroughput exception")
    void getThroughputTest_when_readThroughput_exception() throws IOException, InterruptedException, ExecutionException {
        final var msg = "cannot read";
        doThrow(new IOException(msg)).when(commandService).readThroughput();
        testException(HttpStatus.INTERNAL_SERVER_ERROR, msg, () -> statusService.getThroughput());
        verify(commandService, times(1)).readThroughput();
    }

}
