package com.aberdote.OVPN4ALL.utils.service.impl;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.impl.ConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConfigServiceTest {

    @Mock
    private  ConfigRepository configRepository;
    @Mock
    private  CommandService commandService;
    private ConfigService configService;
    private final static SetupDTO SETUP =
            new SetupDTO("444", "10.0.0.1", "255.255.255.0", "203.1.1.2");

    @BeforeEach
    void initConfigService() {
        configService = new ConfigServiceImpl(configRepository, commandService);
    }

    @Test
    void getConfigOk() {
        final ConfigEntity chosenConfig = new ConfigEntity("762", "192.168.0.1", "255.255.255.0", "204.65.2.1");
        final List<ConfigEntity> configEntityList = List.of(
                chosenConfig,
                new ConfigEntity("456", "192.168.1.1", "255.255.255.0", "204.65.2.2"),
                new ConfigEntity("189", "192.168.2.1", "255.255.0.0", "204.65.2.3"),
                new ConfigEntity("41", "192.168.3.1", "255.255.0.0", "204.65.2.4"),
                new ConfigEntity("25", "192.168.5.1", "255.255.0.0", "204.65.2.5")
        );
        when(configRepository.findAll()).thenReturn(configEntityList);
        SetupDTO foundConfig = configService.getConfig();
        assertEquals(chosenConfig.getGateway(), foundConfig.getGateway());
        assertEquals(chosenConfig.getPort(), foundConfig.getPort());
        assertEquals(chosenConfig.getNetmask(), foundConfig.getSubnet());
        assertEquals(chosenConfig.getServer(), foundConfig.getServer());
        verify(configRepository, times(1)).findAll();
    }

    @Test
    void getConfigKO() {
        when(configRepository.findAll()).thenReturn(Collections.emptyList());
        final CustomException customException = assertThrows(CustomException.class, () -> configService.getConfig());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        assertEquals("No setup was configured", customException.getError());
        verify(configRepository, times(1)).findAll();
    }

    @Test
    void setConfigOk() throws IOException, InterruptedException {
        when(commandService.addConfig(anyString(), anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(configRepository.findAll()).thenReturn(List.of(new ConfigEntity()));
        when(commandService.isActive()).thenReturn(Boolean.TRUE, Boolean.TRUE);
        final SetupDTO obtainedSetup = configService.setConfig(SETUP);
        assertEquals(SETUP, obtainedSetup);
        verify(commandService, times(1)).addConfig(anyString(), anyString(), anyString());
        verify(configRepository, times(1)).findAll();
        verify(configRepository, times(1)).save(any(ConfigEntity.class));
        verify(commandService, times(2)).isActive();
        verify(commandService, times(1)).shutdown();
        verify(commandService, times(1)).startUp();
    }

    @Test
    void setConfigNull() {
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(null));
        assertEquals("Cannot save setup, is null", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
    }

    @Test
    void setConfigInvalidPort() {
        SETUP.setPort("2 ");
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot save setup, 2  is not a valid port", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
    }

    @Test
    void setConfigInvalidGateway() {
        SETUP.setGateway("200.201.100.1");
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot save setup, 200.201.100.1 is not a valid private ip address", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
    }

    @Test
    void setConfigInvalidSubnet() {
        SETUP.setSubnet("192.168.9.12");
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot save setup, 192.168.9.12 is not a valid netmask", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
    }

    @Test
    void setConfigInvalidServer() {
        SETUP.setServer("10.254.1.2");
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot save setup, 10.254.1.2 is not a valid public ip address", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
    }

    @Test
    void setConfigInvalidCommandExec() throws IOException, InterruptedException {
        when(commandService.addConfig(anyString(), anyString(), anyString())).thenReturn(Boolean.FALSE);
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot setup config, script failed", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(commandService, times(1)).addConfig(anyString(), anyString(), anyString());
        verify(configRepository, never()).findAll();
        verify(configRepository, never()).save(any(ConfigEntity.class));
        verify(commandService, never()).isActive();
        verify(commandService, never()).shutdown();
        verify(commandService, never()).startUp();
    }

    @Test
    void setConfigExceptionCommandExec() throws IOException, InterruptedException {
        when(commandService.addConfig(anyString(), anyString(), anyString())).thenThrow(new IOException("Test"));
        final CustomException customException = assertThrows(CustomException.class, () -> configService.setConfig(SETUP));
        assertEquals("Cannot execute set config script, ErrorMessage: 'Test'", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(commandService, times(1)).addConfig(anyString(), anyString(), anyString());
        verify(configRepository, never()).findAll();
        verify(configRepository, never()).save(any(ConfigEntity.class));
        verify(commandService, never()).isActive();
        verify(commandService, never()).shutdown();
        verify(commandService, never()).startUp();
    }

}
