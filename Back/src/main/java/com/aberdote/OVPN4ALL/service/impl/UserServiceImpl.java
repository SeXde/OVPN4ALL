package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.common.constanst.UserReservedConstants;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponsePageDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import com.aberdote.OVPN4ALL.utils.converter.PageConverter;
import com.aberdote.OVPN4ALL.utils.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service
@Transactional @RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final CommandService commandService;
    private final ConfigService configService;

    @Override
    public UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO) {
        validateUser(createUserRequestDTO);
        try {
            if (!commandService.addUser(createUserRequestDTO.getName(), createUserRequestDTO.getPassword())) {
                throw new CustomException(String.format("Cannot add user '%s', execution failed, see logs for more details", createUserRequestDTO.getName()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Adding user {}", createUserRequestDTO.getName());
            final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
            userEntity.setRoles(createUserRequestDTO.getRoles().stream()
                    .map(role -> roleRepository.findByRoleName(role.getRoleName())
                            .orElseThrow(() -> {
                                log.error("Cannot add user {}, role {} doesn't exist", createUserRequestDTO.getName(), role.getRoleName());
                                throw new CustomException("Cannot add user "+createUserRequestDTO.getName()+", role "+role.getRoleName()+" doesn't exist", HttpStatus.BAD_REQUEST);
                            }))
                    .collect(Collectors.toSet())
            );
            userEntity.setUser(userEntity.getRoles().stream().map(RoleEntity::getRoleName).anyMatch(role -> role.equals(RoleConstants.ROLE_USER)));
            userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
            return EntityConverter.fromUserEntityToUserResponseDTO(userRepository.save(userEntity));
        } catch (IOException | InterruptedException e) {
            final String message = String.format("Cannot execute add user script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void addAllUsers(List<CreateUserRequestDTO> createUsersRequestDTO) {
        createUsersRequestDTO.forEach(this::addUser);
    }

    @Override
    public void deleteUser(String userName) {
        deleteUser(this.userRepository.findByNameIgnoreCase(userName));
    }

    @Override
    public void deleteUser(Long id){this.deleteUser(userRepository.findById(id));
    }

    @Override
    public UserResponseDTO getUser(String userName) {
       return this.getUser(userRepository.findByNameIgnoreCase(userName));
    }

    @Override
    public UserResponseDTO getUser(Long id) {
        return this.getUser(userRepository.findById(id));
    }

    @Override
    public Collection<UserResponseDTO> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll().stream().map(EntityConverter::fromUserEntityToUserResponseDTO).toList();
    }

    @Override
    public UserResponsePageDTO getUsersPaginated(int pageNumber, int usersPerPage) {
        return PageConverter.fromPagetoUserResponsePageDTO(userRepository.findAllByOrderByIsUserDesc(PageRequest.of(pageNumber, usersPerPage)).map(EntityConverter::fromUserEntityToUserResponseDTO));
    }

    @Override
    public void validateUser(LoginUserRequestDTO loginUserRequestDTO) {
        if (loginUserRequestDTO == null) {
            final String msg = "User cannot be null";
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        Optional<UserEntity> optUser = userRepository.findByNameIgnoreCase(loginUserRequestDTO.getName());
        if (optUser.isEmpty()){
            log.error("User {} does not exist", loginUserRequestDTO.getName());
            throw new CustomException("User "+loginUserRequestDTO.getName()+" does not exist", HttpStatus.NOT_FOUND);
        }
        final UserEntity user = optUser.get();
        if (!this.isAdmin(user)){
            log.error("User {} has not privileges", user.getName());
            throw new CustomException("User "+user.getName()+" has not privileges", HttpStatus.UNAUTHORIZED);
        }
        if (!bCryptPasswordEncoder.matches(loginUserRequestDTO.getPassword(), user.getPassword())){
            log.info("Password is not correct for user {}", user.getName());
            throw new CustomException("Password is not correct for user "+user.getName(), HttpStatus.UNAUTHORIZED);
        }
        log.info("User {} has been validated", user.getName());
    }

    @Override
    public UserResponseDTO addRoleToUser(String sender, String receiver, String roleName) {
        Optional<UserEntity> senderUserOpt = userRepository.findByNameIgnoreCase(sender);
        if (senderUserOpt.isEmpty()) {
            log.error("User {} does not exist", sender);
            throw new CustomException("User "+sender+" does not exist", HttpStatus.NOT_FOUND);
        }
        Optional<RoleEntity> roleEntityOpt = roleRepository.findByRoleName(roleName);
        if (roleEntityOpt.isEmpty()) {
            log.error("Role {} not found", roleName);
            throw new CustomException("Role "+roleName+" not found", HttpStatus.NOT_FOUND);
        }
        UserEntity senderUser = senderUserOpt.get();
        if (!UserValidator.validateRole(senderUser, roleName)) {
            final String msg = String.format("User %s is not allowed to add role %s", senderUser.getName(), roleName);
            log.error(msg);
            throw new CustomException(msg, HttpStatus.NOT_FOUND);
        }
        return userRepository.findByNameIgnoreCase(receiver)
                .map(user -> {
                    user.getRoles().add(roleEntityOpt.get());
                    // userRepository.save(user);
                    log.info("User "+receiver+" has got new role: "+roleName);
                    return EntityConverter.fromUserEntityToUserResponseDTO(user);
                })
                .orElseGet(() -> {
                    log.error("User {} does not exist", receiver);
                    throw new CustomException("User "+receiver+" does not exist", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public File downloadUserVPN(String user) {
        return downloadUserVPN(userRepository.findByNameIgnoreCase(user));
    }

    @Override
    public File downloadUserVPN(Long id) {
        return downloadUserVPN(userRepository.findById(id));
    }

    @Override
    public void disconnectUser(String userName) {
        disconnectUser(userRepository.findByNameIgnoreCase(userName));
    }

    @Override
    public void disconnectUser(Long id) {
        disconnectUser(userRepository.findById(id));
    }

    private File downloadUserVPN(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isEmpty()) {
            final String msg = "Cannot find user to download vpn";
            log.error(msg);
            throw new CustomException(msg, HttpStatus.NOT_FOUND);
        }
        final UserEntity userEntity = optionalUserEntity.get();
        try {
            final File ovpnFile = commandService.downloadOVPNFile(userEntity.getName(), configService.getConfig());
            if (ovpnFile == null || !ovpnFile.exists()) {
                final String msg = "Cannot create user config, for more details check logs";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return ovpnFile;
        } catch (IOException | InterruptedException e) {
            final String msg = String.format("Cannot download config file, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isRole(UserEntity userEntity, String roleName) {
        return userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList()
                .contains(roleName);
    }

    private boolean isAdmin(UserEntity userEntity) {
        return isRole(userEntity, RoleConstants.ROLE_ADMIN);
    }

    private boolean isUser(UserEntity userEntity) {
        return isRole(userEntity, RoleConstants.ROLE_USER);
    }

    private boolean isLastAdmin(UserEntity userEntity) {
        return  this.isAdmin(userEntity)
                && userRepository.findAll()
                .stream()
                .filter(this::isAdmin)
                .toList()
                .size() == 1;
    }

    private void deleteUser(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isEmpty()) {
            log.error("Cannot find user to delete");
            throw new CustomException("Cannot find user to delete", HttpStatus.NOT_FOUND);
        }
        final UserEntity userEntity = optionalUserEntity.get();
        if (this.isLastAdmin(userEntity)) {
            log.error("User is last Admin");
            throw new CustomException("User is last Admin", HttpStatus.FORBIDDEN);
        }
        try {
            if (commandService.isActive()) commandService.killClient(userEntity.getName());
            if (!commandService.deleteUser(userEntity.getName())) {
                throw new CustomException(String.format("Cannot delete user '%s', execution failed, see logs for more details", userEntity.getName()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            log.info("Deleting user {}", userEntity.getName());
            userRepository.delete(optionalUserEntity.get());
        } catch (IOException | InterruptedException e) {
            final String message = String.format("Cannot execute delete user script, ErrorMessage: '%s'", e.getMessage());
            log.error(message);
            throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void disconnectUser(Optional<UserEntity> optionalUserEntity) {
        optionalUserEntity.ifPresentOrElse((user -> {
            try {
                commandService.killClient(user.getName());
            } catch (IOException | InterruptedException e) {
                final String message = String.format("Cannot disconnect user, ErrorMessage: '%s'", e.getMessage());
                log.error(message);
                throw new CustomException(message, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }), () -> {
            log.error("Cannot find user to delete");
            throw new CustomException("Cannot find user to delete", HttpStatus.NOT_FOUND);
        });
    }

    private UserResponseDTO getUser(Optional<UserEntity> optionalUserEntity) {
        return optionalUserEntity
            .map(user -> {
                log.info("Getting user {}", optionalUserEntity.get().getName());
                return EntityConverter.fromUserEntityToUserResponseDTO(user);
            })
            .orElseThrow(() -> new CustomException("Cannot get user, it doesn't exists", HttpStatus.NOT_FOUND));
    }

    private void validateUser(CreateUserRequestDTO createUserRequestDTO) {
        String msg = null;
        if (createUserRequestDTO == null) {
            msg = "User is null";
        }
        else if (UserReservedConstants.PASSWORD_FORBIDDEN.contains(createUserRequestDTO.getPassword())) {
            msg = String.format("Password %s is not valid", createUserRequestDTO.getPassword());
        }
        else if (!UserValidator.validateEmail(createUserRequestDTO.getEmail())) {
            msg = String.format("Email %s is not valid", createUserRequestDTO.getEmail());
        }
        else if (userRepository.findByNameIgnoreCase(createUserRequestDTO.getName()).isPresent()) {
            msg = String.format("User %s is already registered", createUserRequestDTO.getName());
        }
        else if (createUserRequestDTO.getRoles() == null ||createUserRequestDTO.getRoles().isEmpty()) {
            msg  = "Roles cannot be empty or null";
        }
        if (msg != null) {
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
    }

}
