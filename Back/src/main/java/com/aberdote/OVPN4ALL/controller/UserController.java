package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

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

    // TODO paginate users
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))}),
            @ApiResponse(responseCode = "500", description = "Error trying to get all users", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class))})
    })
    @GetMapping("")
    public ResponseEntity<Collection<UserResponseDTO>> getUsers() {
        log.info("Request to get all users");
        final Collection<UserResponseDTO> userEntityList = userService.getUsers();
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
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
            @ApiResponse(responseCode = "202", description = "User logged in"),
            @ApiResponse(responseCode = "400", description = "Wrong data was passed", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Wrong user credentials", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to log in user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @PostMapping("/signIn")
    public ResponseEntity<Void> signIn(@RequestBody LoginUserRequestDTO loginDTO) {
        log.info("Request to authenticate user {}", loginDTO.getName());
        userService.validateUser(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "400", description = "Wrong Parameter", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))}),
            @ApiResponse(responseCode = "500", description = "Error trying to delete user", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ErrorDTO.class)))})
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "The id of the user") @PathVariable(required = true) Long id) {
        log.info("Request to delete user {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

}
