package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j @Service @RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final CommandService commandService;

    @Override
    public boolean isActive() {
        try {
            log.debug("Trying to fetch vpn status");
            final boolean active =  commandService.isActive();
            log.debug("Status fetched correctly with value {}", active);
            return  active;
        } catch (Exception e) {
            final String msg = String.format("Cannot fetch vpn status, got ErrorMessage:  %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean turnOn() {
        try {
            commandService.startUp();
            if (!commandService.isActive()) {
                throw new CustomException("Cannot start openvpn", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return true;
        } catch (IOException | InterruptedException e) {
            final String message = String.format("Cannot execute start openvpn script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean turnOff() {
        try {
            if (!commandService.shutdown()) {
                throw new CustomException("Cannot stop openvpn", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return true;
        } catch (IOException | InterruptedException e) {
            final String message = String.format("Cannot execute stop openvpn script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
