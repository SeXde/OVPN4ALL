package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController @RequestMapping("/api/setup") @CrossOrigin(maxAge = 3600)
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping(path = "")
    public ResponseEntity<SetupDTO> getSetup() {
        log.info("Request to get server config");
        SetupDTO setupDTO = configService.getConfig();
        return new ResponseEntity<>(setupDTO, HttpStatus.OK);
    }

    @PostMapping("") @Validated
    public ResponseEntity<ErrorDTO> setupServer(@RequestBody SetupDTO setupDTO) {
        log.info("Request to save server config");
        ErrorDTO error = configService.setConfig(setupDTO);
        return new ResponseEntity<>(error, HttpStatus.ACCEPTED);
    }

}
