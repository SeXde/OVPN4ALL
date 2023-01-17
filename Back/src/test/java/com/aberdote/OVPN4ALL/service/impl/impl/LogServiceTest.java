package com.aberdote.OVPN4ALL.service.impl.impl;

import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class LogServiceTest {

    @Mock
    private CommandService commandService;
    @Mock
    private UserRepository userRepository;
    private LogService logService;

    @BeforeEach
    public void init() {
        logService = new LogServiceImpl(commandService, userRepository);
    }


    @Test
    public void testOVPNLog() {
        testLogFile(logService::getOVPNLog, 1);
    }

    private void testLogFile(Function<Integer, String> method, Integer lines) {
        final String content = method.apply(lines);
        assertNotNull(content);
        assertFalse(content.isEmpty());
    }


}
