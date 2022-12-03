package com.aberdote.OVPN4ALL.service.impl;


import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.utils.validator.config.ConfigValidator;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j @Service @Transactional @RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;
    private final CommandService commandService;

    @Override
    public SetupDTO getConfig() {
        log.info("Getting setup");
        return configRepository.findAll()
                .stream()
                .findFirst()
                .map(EntityConverter::fromConfigEntityToSetupDTO)
                .orElseThrow(() -> {
                    log.error("No setup was configured");
                    throw new CustomException("No setup was configured", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public SetupDTO setConfig(SetupDTO setupDTO) {
        if (setupDTO == null) {
            final String msg = "Cannot save setup, is null";
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validatePort(setupDTO.getPort())) {
            final String msg = String.format("Cannot save setup, %s is not a valid port", setupDTO.getPort());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validatePrivateIp(setupDTO.getGateway())) {
            final String msg = String.format("Cannot save setup, %s is not a valid private ip address", setupDTO.getGateway());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validateNetmask(setupDTO.getSubnet())) {
            final String msg = String.format("Cannot save setup, %s is not a valid netmask", setupDTO.getSubnet());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validatePublicIp(setupDTO.getServer())) {
            final String msg = String.format("Cannot save setup, %s is not a valid public ip address", setupDTO.getServer());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        try {
            if (!commandService.addConfig(setupDTO.getPort(), setupDTO.getGateway(), setupDTO.getSubnet())) {
                throw new CustomException("Cannot setup config, script failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Setting config");
            if (!configRepository.findAll().isEmpty()) configRepository.deleteAll();
            log.info("Saving new setup");
            configRepository.save(EntityConverter.fromSetupDTOToConfigEntity(setupDTO));
            if (commandService.isActive()) {
                do {
                    commandService.shutdown();
                    commandService.startUp();
                    Thread.sleep(500);
                } while (!commandService.isActive());
            }
            return setupDTO;
        } catch (IOException | InterruptedException e) {
            final String message = String.format("Cannot execute set config script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
