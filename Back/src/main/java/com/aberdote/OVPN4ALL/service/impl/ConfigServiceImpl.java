package com.aberdote.OVPN4ALL.service.impl;


import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.util.Converter;
import com.aberdote.OVPN4ALL.util.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                    return null;
                });
    }

    @Override
    public ErrorDTO setConfig(SetupDTO setupDTO) {
        ErrorDTO error = new ErrorDTO(null);
        // config stuff
        if (!Validator.validatePort(setupDTO.getPort())) {
            error.setError("Port is not valid.");
            log.error("Cannot save setup, port is not valid");
            return error;
        }
        if (!Validator.validateIp(setupDTO.getGateway())) {
            error.setError("Gateway is not valid.");
            log.error("Cannot save setup, gateway is not valid");
            return error;
        }
        if (!Validator.validateNetmask(setupDTO.getSubnet())) {
            error.setError("Netmask is not valid.");
            log.error("Cannot save setup, netmask is not valid");
            return error;
        }
        if (!configRepository.findAll().isEmpty()) configRepository.deleteAll();
        log.info("Saving new setup");
        configRepository.save(Converter.convertFromDTOSetup(setupDTO));
        return error;
    }
}
