package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponsePageDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Service
public interface UserService {
    UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO);

    void addAllUsers(List<CreateUserRequestDTO> createUsersRequestDTO);
    void deleteUser(String userName);
    void deleteUser(Long id);
    UserResponseDTO getUser(String userName);
    UserResponseDTO getUser(Long id);
    Collection<UserResponseDTO> getUsers();
    UserResponsePageDTO getUsersPaginated(int pageNumber, int usersPerPage);
    void validateUser(LoginUserRequestDTO loginUserRequestDTO);
    UserResponseDTO addRoleToUser(String sender, String receiver, String roleName);
    File downloadUserVPN(String user);
    File downloadUserVPN(Long id);

    void disconnectUser(String userName);
    void disconnectUser(Long id);

    UserResponseDTO registerFirstUser(CreateUserRequestDTO newUser);

    Boolean noUsers();
}
