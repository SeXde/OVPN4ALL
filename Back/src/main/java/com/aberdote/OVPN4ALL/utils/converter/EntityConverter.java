package com.aberdote.OVPN4ALL.utils.converter;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailResponseDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.entity.MailEntity;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;

public class EntityConverter {

    public static UserResponseDTO fromUserEntityToUserResponseDTO(UserEntity userEntity) {
        return new UserResponseDTO(userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getRoles()
                    .stream()
                    .map(roleEntity -> new RoleDTO(roleEntity.getRoleName()))
                    .toList(),
                userEntity.getCreatedAt());
    }

    public static UserEntity fromCreateUserDTOToUserEntity(CreateUserRequestDTO createUserDTO) {
        return new UserEntity(createUserDTO.getName(),
                createUserDTO.getEmail(),
                createUserDTO.getPassword()
        );
    }

    public static SetupDTO fromConfigEntityToSetupDTO(ConfigEntity configEntity) {
        return new SetupDTO(configEntity.getPort(), configEntity.getGateway(), configEntity.getNetmask(), configEntity.getServer());
    }

    public static RoleDTO fromRoleEntityToRoleDTO(RoleEntity roleEntity) {
        return new RoleDTO(roleEntity.getRoleName());
    }

    public static RoleEntity fromRoleDTOtoRoleEntity(RoleDTO roleDTO) {
        return new RoleEntity(roleDTO.getRoleName());
    }

    public static ConfigEntity fromSetupDTOToConfigEntity(SetupDTO setupDTO) {
        return new ConfigEntity(setupDTO.getPort(), setupDTO.getGateway(), setupDTO.getSubnet(), setupDTO.getServer());
    }

    public static MailResponseDTO fromMailEntityToMailResponseDTO(MailEntity mailEntity) {
        return new MailResponseDTO(mailEntity.getSmtpHost(), mailEntity.getSmtpPort(), mailEntity.isTtl(), mailEntity.getUsername());
    }

    public static MailRequestDTO fromMailEntityToMailRequestDTO(MailEntity mailEntity) {
        return new MailRequestDTO(mailEntity.getSmtpHost(), mailEntity.getSmtpPort(), mailEntity.isTtl(), mailEntity.getUsername(), mailEntity.getPassword());
    }

    public static MailEntity fromMailRequestDTOToMailEntity(MailRequestDTO mailRequestDTO) {
        return new MailEntity(mailRequestDTO.getSmtpHost(), mailRequestDTO.getSmtpPort(), mailRequestDTO.isTtl(), mailRequestDTO.getUsername(), mailRequestDTO.getPassword());
    }

}
