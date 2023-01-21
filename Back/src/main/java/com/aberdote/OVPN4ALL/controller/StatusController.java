package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.BandwidthDTO;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @Operation(summary = "Get current server status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status was fetched"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to fetch status", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "")
    public ResponseEntity<Boolean> getStatus() {
        return ResponseEntity.ok(statusService.isActive());
    }

    @Operation(summary = "Get current server status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Could start openvpn"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to start openvpn", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/on")
    public ResponseEntity<Void> startOpenvpn() {
        statusService.turnOn();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get current server status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Could stop openvpn"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to stop openvpn", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/off")
    public ResponseEntity<Void> shutdownOpenvpn() {
        statusService.turnOff();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/bandwidth")
    public ResponseEntity<BandwidthDTO> getBandwidth() {
        return ResponseEntity.ok(statusService.getThroughput());
    }


}
