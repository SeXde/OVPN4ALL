package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.service.ConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController @RequiredArgsConstructor
@RequestMapping("/api/setup") @CrossOrigin(maxAge = 3600)
@Api(value="Server configuration API", tags = {"With this API we can set server configuration or change it once it's configured"})
public class ConfigController {

    private ConfigService configService;

    @ApiOperation(value = "Get current server configuration if present", response = SetupDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Configuration was found", response = SetupDTO.class),
            @ApiResponse(code = 400, message = "Wrong data was passed", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 404, message = "Configuration not found", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to find configuration", response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(path = "")
    public ResponseEntity<SetupDTO> getSetup() {
        log.info("Request to get server config");
        final SetupDTO setupDTO = configService.getConfig();
        return new ResponseEntity<>(setupDTO, HttpStatus.OK);
    }

    @ApiOperation(value = "Set or change server configuration", response = SetupDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Configuration was found", response = SetupDTO.class),
            @ApiResponse(code = 400, message = "Wrong data was passed", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to set configuration", response = ErrorDTO.class)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("") @Validated
    public ResponseEntity<SetupDTO> setupServer(@RequestBody SetupDTO setupDTO) {
        log.info("Request to save server config");
        final SetupDTO responseSetupDTO = configService.setConfig(setupDTO);
        return new ResponseEntity<>(responseSetupDTO, HttpStatus.CREATED);
    }

}
