package com.aberdote.OVPN4ALL.util;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;

import java.util.stream.Collectors;

public class Converter {

    public static UserResponseDTO convertDTOUser(UserEntity userEntity) {
        return new UserResponseDTO(userEntity.getId(),
                userEntity.getName(),
                userEntity.getRoles()
                    .stream()
                    .map(roleEntity -> new RoleDTO(roleEntity.getRoleName()))
                    .toList(),
                userEntity.getCreatedAt());
    }

    public static UserEntity convertFromDTOUser(CreateUserRequestDTO createUserDTO) {
        return new UserEntity(createUserDTO.getName(),
                createUserDTO.getPassword(),
                createUserDTO.getRoles()
                        .stream()
                        .map(roleDTO -> new RoleEntity(roleDTO.getRoleName()))
                        .collect(Collectors.toSet()));
    }

    public static SetupDTO convertDTOSetup(ConfigEntity configEntity) {
        return new SetupDTO(configEntity.getPort(), configEntity.getGateway(), configEntity.getNetmask());
    }

    public static ConfigEntity convertFromDTOSetup(SetupDTO setupDTO) {
        return new ConfigEntity(setupDTO.getPort(), setupDTO.getGateway(), setupDTO.getSubnet());
    }

}
