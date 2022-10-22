package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface UserService {
    UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO);
    void deleteUser(String userName);
    void deleteUser(Long id);
    UserResponseDTO getUser(String userName);
    UserResponseDTO getUser(Long id);
    Collection<UserResponseDTO> getUsers();
    Page<UserResponseDTO> getUsersPaginated(int pageNumber, int usersPerPage);
    void validateUser(LoginUserRequestDTO loginUserRequestDTO);
    UserResponseDTO addRoleToUser(String sender, String receiver, String roleName);
}
