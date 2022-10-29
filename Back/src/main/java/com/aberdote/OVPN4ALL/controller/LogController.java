package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RequiredArgsConstructor @Transactional @Slf4j
@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    @Operation(summary = "Download server logs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Server logs found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Logs found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to download OVPN file", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @GetMapping("")
    public ResponseEntity<Resource> downloadLogs() {
        log.info("Request to download logs");
        final ByteArrayResource logsZip = logService.downloadLogs();
        return ResponseEntity.ok()
                .contentLength(logsZip.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(logsZip);
    }


}
