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
        // config stuff
        if (!ConfigValidator.validatePort(setupDTO.getPort())) {
            log.error("Cannot save setup, port is not valid");
            throw new CustomException("Port is not valid", HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validateIp(setupDTO.getGateway())) {
            log.error("Cannot save setup, gateway is not valid");
            throw new CustomException("Gateway is not valid", HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validateNetmask(setupDTO.getSubnet())) {
            log.error("Cannot save setup, netmask is not valid");
            throw new CustomException("Netmask is not valid", HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validateIp(setupDTO.getServer())) {
            log.error("Cannot save setup, server public ip is not valid");
            throw new CustomException("Server public ip is not valid", HttpStatus.BAD_REQUEST);
        }
        try {
            if (!commandService.addConfig(setupDTO.getPort(), setupDTO.getGateway(), setupDTO.getSubnet())) {
                throw new CustomException("Cannot setup config", HttpStatus.INTERNAL_SERVER_ERROR);
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
