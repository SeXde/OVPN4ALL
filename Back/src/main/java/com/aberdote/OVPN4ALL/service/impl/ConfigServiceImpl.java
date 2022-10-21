package com.aberdote.OVPN4ALL.service.impl;


import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.util.Converter;
import com.aberdote.OVPN4ALL.util.validator.config.ConfigValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j @Service @RequiredArgsConstructor @Transactional
public class ConfigServiceImpl implements ConfigService {

    private final ConfigRepository configRepository;

    @Override
    public SetupDTO getConfig() {
        log.info("Getting setup");
        return configRepository.findAll()
                .stream()
                .findFirst()
                .map(Converter::convertDTOSetup)
                .orElseGet(() -> {
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
        if (!configRepository.findAll().isEmpty()) configRepository.deleteAll();
        log.info("Saving new setup");
        configRepository.save(Converter.convertFromDTOSetup(setupDTO));
        return setupDTO;
    }
}
