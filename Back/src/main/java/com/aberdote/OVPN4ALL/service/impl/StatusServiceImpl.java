package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.BandwidthDTO;
import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.StatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j @Service @RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {

    private final CommandService commandService;
    private final ConfigService configService;

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
            configService.getConfig();
            do {
                commandService.shutdown();
                commandService.startUp();
                Thread.sleep(500);
            } while(!commandService.isActive());
            return true;
        } catch (CustomException ce) {
            final String message = String.format("Cannot execute start openvpn script, ErrorMessage: '%s'", ce.getError());
            log.error(message);
            throw new CustomException(message, ce.getHttpStatus());
        } catch (Exception e) {
            final String message = String.format("Cannot execute start openvpn script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean turnOff() {
        try {
            configService.getConfig();
            do {
                commandService.shutdown();
            } while(commandService.isActive());
            return true;
        } catch (CustomException e) {
            final String message = String.format("Cannot execute stop openvpn script, ErrorMessage: '%s'", e.getError());
            log.error(message);
            throw new CustomException(message, e.getHttpStatus());
        } catch (Exception e) {
            final String message = String.format("Cannot execute stop openvpn script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public BandwidthDTO getThroughput() {
        try {
            final String[] throughput = commandService.readThroughput();
            if (throughput.length == 0) return  new BandwidthDTO(0.0f, 0.0f);
            return new BandwidthDTO(Float.parseFloat(throughput[0]), Float.parseFloat(throughput[1]));
        } catch (IOException | InterruptedException | ExecutionException e) {
            final String message = String.format("Cannot read interface throughput, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserConnectionInfoDTO> getUsersConnected() {
        return commandService.getUsersConnected();
    }


}
