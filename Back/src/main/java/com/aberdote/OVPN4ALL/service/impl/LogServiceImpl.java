package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final CommandService commandService;
    @Override
    public File downloadLogs() {
        try {
            final File logs = commandService.downloadLogs();
            if (logs == null || !logs.exists()) {
                final String msg = "Cannot download logs";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return logs;
        } catch (IOException | InterruptedException e) {
            final String msg = String.format("Cannot download logs, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
