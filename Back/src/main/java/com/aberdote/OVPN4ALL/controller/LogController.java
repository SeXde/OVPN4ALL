package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

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
    @GetMapping(value = "", produces = "application/zip")
    public ResponseEntity<Resource> downloadLogs() {
        log.info("Request to download logs");
        final File logs = logService.downloadLogs();
        try {
            final Resource resource = new UrlResource(logs.toURI());
            final HttpHeaders httpHeaders = new HttpHeaders();
            final String fileName = logs.getName();
            httpHeaders.add("File-Name", fileName);
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + fileName);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).headers(httpHeaders).body(resource);
        } catch (MalformedURLException e) {
            final String message = String.format("Cannot send logs, ErrorMessage:%s", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user}/info")
    public ResponseEntity<UserInfoDTO> getUserInfo(@PathVariable String user){
        log.info("Request to get {} info", user);
        final UserInfoDTO userInfoDTO = logService.getUserInfo(user);
        if (userInfoDTO == null) throw new CustomException("User has no info", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(userInfoDTO);
    }

    @GetMapping("/users/info")
    public ResponseEntity<List<UserInfoDTO>> getUsersInfo(){
        log.info("Request to get all users info");
        return ResponseEntity.ok(logService.getAllUsersInfo());
    }

    @GetMapping("/users/connections")
    public ResponseEntity<Integer> getNumberOfUsersConnected(){
        log.info("Request to get number of users connected");
        return ResponseEntity.ok(logService.getNumberOfUsersConnected());
    }

    @GetMapping("/users/connected")
    public ResponseEntity<List<UserResponseDTO>> getUsersConnected() {
        log.info("Request to get users connected");
        return ResponseEntity.ok(logService.getUsersConnected());
    }


}
