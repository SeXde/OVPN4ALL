package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import org.springframework.stereotype.Service;

@Service
public interface ConfigService {
    SetupDTO getConfig();
    SetupDTO setConfig(SetupDTO setupDTO) throws CustomException;
}
