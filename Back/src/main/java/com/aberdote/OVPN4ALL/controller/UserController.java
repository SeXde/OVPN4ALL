package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.service.impl.UserServiceImpl;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController @RequiredArgsConstructor
@RequestMapping("/api/users") @CrossOrigin(maxAge = 3600)
@Api(value="User management API", tags = {"With this API we can set server configuration or change it once it's configured"})
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "Save user passed in the body", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "User was saved", response = UserResponseDTO.class),
            @ApiResponse(code = 400, message = "Wrong data was passed", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to save user", response = ErrorDTO.class)
    })
    @PostMapping("")
    public ResponseEntity<UserResponseDTO> saveUser(@RequestBody CreateUserRequestDTO createUserDTO) {
        log.info("Request to save user {}", createUserDTO.getName());
        final UserResponseDTO userResponseDTO = userService.addUser(createUserDTO);
        return new ResponseEntity<>(userResponseDTO, HttpStatus.CREATED);
    }

    // TODO paginate users
    @ApiOperation(value = "Get all users", response = UserResponseDTO.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Users found", response = UserResponseDTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Wrong data was passed", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to get all users", response = ErrorDTO.class)
    })
    @GetMapping("")
    public ResponseEntity<Collection<UserResponseDTO>> getUsers() {
        log.info("Request to get all users");
        final Collection<UserResponseDTO> userEntityList = userService.getUsers();
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }

    @ApiOperation(value = "Get user by id", response = UserResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User found", response = UserResponseDTO.class),
            @ApiResponse(code = 400, message = "Wrong Parameter", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 404, message = "User not found", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to get user", response = ErrorDTO.class)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@ApiParam(value = "The id of the user") @PathVariable(required = true) Long id) {
        log.info("Request to get user {}", id);
        final UserResponseDTO searchedUser = userService.getUser(id);
        return new ResponseEntity<>(searchedUser, HttpStatus.OK);
    }

    @ApiOperation(value = "Log in user")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "User logged in"),
            @ApiResponse(code = 400, message = "Wrong data was passed", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Wrong user credentials", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to log in user", response = ErrorDTO.class)
    })
    @PostMapping("/signIn")
    public ResponseEntity<Void> signIn(@RequestBody LoginUserRequestDTO loginDTO) {
        log.info("Request to authenticate user {}", loginDTO.getName());
        userService.validateUser(loginDTO);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @ApiOperation(value = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User deleted"),
            @ApiResponse(code = 400, message = "Wrong Parameter", response = ErrorDTO.class),
            @ApiResponse(code = 403, message = "Unauthorized access", response = ErrorDTO.class),
            @ApiResponse(code = 500, message = "Error trying to delete user", response = ErrorDTO.class)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@ApiParam(value = "The id of the user") @PathVariable(required = true) Long id) {
        log.info("Request to delete user {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

}
