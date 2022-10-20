package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface UserService {
    ErrorDTO addUser(CreateUserRequestDTO createUserRequestDTO);
    ErrorDTO deleteUser(String userName);
    ErrorDTO deleteUser(Long id);
    UserResponseDTO getUser(String userName);
    UserResponseDTO getUser(Long id);
    Collection<UserResponseDTO> getUsers();
    ErrorDTO validateUser(LoginUserRequestDTO loginUserRequestDTO);
    ErrorDTO addRoleToUser(String sender, String receiver, String roleName);
}
