package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.LogService;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;

import static com.aberdote.OVPN4ALL.TestUtils.testException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LogServiceTest {

    @Mock
    private CommandService commandService;
    private LogService logService;

    @BeforeEach
    void setup() {
        logService = new LogServiceImpl(commandService);
    }

    @DisplayName("Test downloadLogs correct")
    @Test
    void downloadLogsTest_correct() throws IOException, InterruptedException {
        final File file = new File("src/test/resources/exampleFile.txt");
        when(commandService.downloadLogs()).thenReturn(file);
        assertEquals(file, logService.downloadLogs());
    }

    @DisplayName("Test downloadLogs null")
    @Test
    void downloadLogsTest_null() throws IOException, InterruptedException {
        when(commandService.downloadLogs()).thenReturn(null);
        testException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot download logs", () -> logService.downloadLogs());
    }

    @DisplayName("Test downloadLogs not exist")
    @Test
    void downloadLogsTest_not_exist() throws IOException, InterruptedException {
        final File file = Files.newTemporaryFile();
        file.delete();
        when(commandService.downloadLogs()).thenReturn(file);
        testException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot download logs", () -> logService.downloadLogs());
    }

    @DisplayName("Test downloadLogs exception")
    @Test
    void downloadLogsTest_exception() throws IOException, InterruptedException {
        final var msg = "exception xd";
        when(commandService.downloadLogs()).thenThrow(new IOException(msg));
        testException(HttpStatus.INTERNAL_SERVER_ERROR, msg, () -> logService.downloadLogs());
    }

}
