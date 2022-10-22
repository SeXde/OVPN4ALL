package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/setup")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Operation(summary = "Get current server configuration if present")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration was found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SetupDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Configuration not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to find configuration", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @ResponseStatus(HttpStatus.CREATED)
    @GetMapping(path = "")
    public ResponseEntity<SetupDTO> getSetup() {
        log.info("Request to get server config");
        final SetupDTO setupDTO = configService.getConfig();
        return new ResponseEntity<>(setupDTO, HttpStatus.OK);
    }

    @Operation(summary = "Set or change server configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Configuration was found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SetupDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to set configuration", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("") @Validated
    public ResponseEntity<SetupDTO> setupServer(@RequestBody SetupDTO setupDTO) {
        log.info("Request to save server config");
        final SetupDTO responseSetupDTO = configService.setConfig(setupDTO);
        return new ResponseEntity<>(responseSetupDTO, HttpStatus.CREATED);
    }

}
