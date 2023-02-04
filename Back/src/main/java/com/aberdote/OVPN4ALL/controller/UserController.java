package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.common.constanst.UserReservedConstants;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.*;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.security.service.JwtUserDetailsService;
import com.aberdote.OVPN4ALL.security.utils.JwtTokenUtil;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.MalformedURLException;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "Save user passed in the body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was saved", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to save user", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @PostMapping("")
    public ResponseEntity<UserResponseDTO> saveUser(@RequestBody CreateUserRequestDTO createUserDTO) {
        log.info("Request to save user {}", createUserDTO.getName());
        final UserResponseDTO userResponseDTO = userService.addUser(createUserDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Get users by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to get all users", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @GetMapping("")
    public ResponseEntity<UserResponsePageDTO> getUsers(@Parameter(description = "Page number index")@RequestParam(required = true)Integer page,
                                                        @Parameter(description = "NUmber of users retrieved per page")@RequestParam(required = false, defaultValue = UserReservedConstants.MAX_USERS_PER_PAGE)Integer limit) {

        log.info("Request to get page {} with {} users", page, limit);
        final UserResponsePageDTO users = userService.getUsersPaginated(page, limit);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "User not found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to get user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@Parameter(description = "The id of the user") @PathVariable(required = true) Long id) {
        log.info("Request to get user {}", id);
        final UserResponseDTO searchedUser = userService.getUser(id);
        return new ResponseEntity<>(searchedUser, HttpStatus.OK);
    }

    @Operation(summary = "Log in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User logged in", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Wrong user credentials", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to log in user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody LoginUserRequestDTO loginDTO) {
        log.info("Request to authenticate user {}", loginDTO.getName());
        userService.validateUser(loginDTO);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getName());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new JwtResponseDTO(token));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to delete user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponsePageDTO> deleteUser(@Parameter(description = "The id of the user") @PathVariable(required = true) Long id) {
        log.info("Request to delete user {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(userService.getUsersPaginated(0, Integer.parseInt(UserReservedConstants.MAX_USERS_PER_PAGE)));
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to delete user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @DeleteMapping("/name/{name}")
    public ResponseEntity<UserResponsePageDTO> deleteUserWithName(@Parameter(description = "The id of the user") @PathVariable(required = true) String name) {
        log.info("Request to delete user {}", name);
        userService.deleteUser(name);
        return ResponseEntity.ok(userService.getUsersPaginated(0, Integer.parseInt(UserReservedConstants.MAX_USERS_PER_PAGE)));
    }

    @Operation(summary = "Test if token is working")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is working"),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to test token", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @GetMapping("/token")
    public ResponseEntity<Void> testToken() {
        log.info("Testing token ...");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Download OVPN file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Config file found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "Config not found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to download OVPN file", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @GetMapping("/{id}/ovpn")
    public ResponseEntity<Resource> downloadOVPNFile(@Parameter(description = "The id of the user") @PathVariable(required = true) Long id, HttpServletResponse response) {
        log.info("Request to download OPVNFile for user {}", id);
        final File ovpnFile = userService.downloadUserVPN(id);
        try {
            final Resource resource = new UrlResource(ovpnFile.toURI());
            final HttpHeaders httpHeaders = new HttpHeaders();
            final String fileName = StringConverter.fromHexToString(ovpnFile.getName().replace(".ovpn", "")) + ".ovpn";
            httpHeaders.add("File-Name", fileName);
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=" + fileName);
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).headers(httpHeaders).body(resource);
        } catch (MalformedURLException | DecoderException e) {
            final String message = String.format("Cannot send file, ErrorMessage:%s", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/disconnect/{userName}")
    public ResponseEntity<Void> disconnectUser(@PathVariable String userName) {
        userService.disconnectUser(userName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/firstUser")
    public ResponseEntity<UserResponseDTO> registerFirstUser(@RequestBody CreateUserRequestDTO newUser) {
        log.info("Received request to register new user {}", newUser.getName());
        return ResponseEntity.ok().body(userService.registerFirstUser(newUser));
    }

}
