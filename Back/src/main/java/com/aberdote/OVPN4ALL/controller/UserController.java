package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController @RequestMapping("/api/users") @CrossOrigin(maxAge = 3600)
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<ErrorDTO> saveUser(@RequestBody CreateUserRequestDTO createUserDTO) {
        log.info("Request to save user "+createUserDTO.getName());
        ErrorDTO errorDTO = userService.addUser(createUserDTO);
        HttpStatus status = errorDTO.getError() == null ? HttpStatus.CREATED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(errorDTO, status);
    }

    @GetMapping("")
    public ResponseEntity<Collection<UserResponseDTO>> getUsers() {
        log.info("Request to get all users");
        Collection<UserResponseDTO> userEntityList = userService.getUsers();
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long id) {
        log.info("Request to get user " + id);
        UserResponseDTO searchedUser = userService.getUser(id);
        return new ResponseEntity<>(searchedUser, HttpStatus.OK);
    }

    @PostMapping("/signIn")
    public ResponseEntity<ErrorDTO> signIn(@RequestBody LoginUserRequestDTO loginDTO) {
        log.info("Request to authenticate user "+loginDTO.getName());
        ErrorDTO errorDTO = userService.validateUser(loginDTO);
        return new ResponseEntity<>(errorDTO, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ErrorDTO> deleteUser(@PathVariable Long id) {
        log.info("Request to delete user "+id);
        ErrorDTO errorDTO = userService.deleteUser(id);
        HttpStatus status = errorDTO.getError() == null ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT;
        return new ResponseEntity<>(errorDTO, status);
    }

}
