package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final CommandService commandService;
    @Override
    public ByteArrayResource downloadLogs() {
        try {
            final File logZip = commandService.downloadLogs();
            ByteArrayResource resource = null;
            try {
                resource = new ByteArrayResource(Files.readAllBytes(logZip.toPath()));
            } catch (IOException e) {
                final String msg = String.format("Cannot download config file, ErrorMessage: %s", e.getMessage());
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return resource;
        } catch (IOException e) {
            final String msg = String.format("Cannot download config file, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
