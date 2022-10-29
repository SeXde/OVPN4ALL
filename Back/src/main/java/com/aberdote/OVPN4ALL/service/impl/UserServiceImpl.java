package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.common.constanst.UserReservedConstants;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.validator.converter.EntityConverter;
import com.aberdote.OVPN4ALL.utils.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
                                throw new CustomException("Cannot add user "+createUserRequestDTO.getName()+" role "+role.getRoleName()+" doesn't exist", HttpStatus.BAD_REQUEST);
                            }))
                    .collect(Collectors.toSet())
            );
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
    public Page<UserResponseDTO> getUsersPaginated(int pageNumber, int usersPerPage) {
        final List<UserResponseDTO> userResponseDTOList = userRepository.findAll(PageRequest.of(pageNumber, usersPerPage)).stream().map(EntityConverter::fromUserEntityToUserResponseDTO).sorted((u1, u2) -> {
            final boolean u1ContainsUserRole = u1.getRoles().stream().anyMatch(roleDTO -> roleDTO.getRoleName().equals(RoleConstants.ROLE_USER));
            final boolean u2ContainsUserRole = u2.getRoles().stream().anyMatch(roleDTO -> roleDTO.getRoleName().equals(RoleConstants.ROLE_USER));
            if (u1ContainsUserRole == u2ContainsUserRole) return 0;
            if (u1ContainsUserRole) return -1;
            return 1;
        }).toList();
        return  new PageImpl<>(userResponseDTOList);
    }

    @Override
    public void validateUser(LoginUserRequestDTO loginUserRequestDTO) {
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
        UserEntity senderUser = senderUserOpt.get();
        if (!UserValidator.validateRole(senderUser, roleName)) {
            log.error("User is not allowed to add role {}", roleName);
            throw new CustomException("User "+sender+" does not exist", HttpStatus.NOT_FOUND);
        }
        return userRepository.findByNameIgnoreCase(receiver)
                .map(user -> {
                    Optional<RoleEntity> roleEntityOpt = roleRepository.findByRoleName(roleName);
                    if (roleEntityOpt.isEmpty()) {
                        log.error("Role {} not found", roleName);
                        throw new CustomException("Role "+roleName+" not found", HttpStatus.NOT_FOUND);
                    }
                    user.getRoles().add(roleEntityOpt.get());
                    // userRepository.save(user);
                    log.info("User "+receiver+" has got new role: "+roleName);
                    return EntityConverter.fromUserEntityToUserResponseDTO(user);
                })
                .orElseGet(() -> {
                    log.error("User {} not found", receiver);
                    throw new CustomException("User "+receiver+" not found", HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public ByteArrayResource downloadUserVPN(String user) {
        return downloadUserVPN(userRepository.findByNameIgnoreCase(user));
    }

    @Override
    public ByteArrayResource downloadUserVPN(Long id) {
        return downloadUserVPN(userRepository.findById(id));
    }

    @Override
    public List<File> downloadLogs() {
        try {
            final List<File> logs = commandService.downloadLogs();
            if (logs == null || logs.isEmpty()) {
                final String msg = "There are no logs to download";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.NOT_FOUND);
            }
            return logs;
        } catch (IOException e) {
            final String msg = String.format("Cannot download logs, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ByteArrayResource downloadUserVPN(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isEmpty()) {
            final String msg = "Cannot find user to download vpn";
            log.error(msg);
            throw new CustomException(msg, HttpStatus.NOT_FOUND);
        }
        final UserEntity userEntity = optionalUserEntity.get();
        try {
            final File ovpnFile = commandService.downloadOVPNFile(userEntity.getName());
            if (ovpnFile == null) {
                final String msg = "Cannot create user config, for more details check logs";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            ByteArrayResource resource = null;
            try {
                resource = new ByteArrayResource(Files.readAllBytes(ovpnFile.toPath()));
            } catch (IOException e) {
                final String msg = String.format("Cannot download config file, ErrorMessage: %s", e.getMessage());
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return resource;
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

    private UserResponseDTO getUser(Optional<UserEntity> optionalUserEntity) {
        return optionalUserEntity
            .map(user -> {
                log.info("Getting user {}", optionalUserEntity.get().getName());
                return EntityConverter.fromUserEntityToUserResponseDTO(user);
            })
            .orElseThrow(() -> new CustomException("Cannot get user, it doesn't exists", HttpStatus.NOT_FOUND));
    }

    private void validateUser(CreateUserRequestDTO createUserRequestDTO) {
        if (UserReservedConstants.PASSWORD_FORBIDEN.contains(createUserRequestDTO.getPassword())) {
            log.error("{} is not a valid password", createUserRequestDTO.getEmail());
            throw new CustomException(String.format("%s is not a valid name", createUserRequestDTO.getName()), HttpStatus.BAD_REQUEST);
        }
        if (!UserValidator.validateEmail(createUserRequestDTO.getEmail())) {
            log.error("email {} is not valid", createUserRequestDTO.getEmail());
            throw new CustomException("email "+createUserRequestDTO.getEmail()+" is not valid", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByNameIgnoreCase(createUserRequestDTO.getName()).isPresent()) {
            log.error("Cannot add user {} already exists", createUserRequestDTO.getName());
            throw new CustomException("User "+createUserRequestDTO.getName()+" already exists", HttpStatus.BAD_REQUEST);
        }
        if (!createUserRequestDTO.getRoles().stream().allMatch(role -> roleRepository.findByRoleName(role.getRoleName()).isPresent())) {
            log.error("Cannot add user {} some roles are not correct", createUserRequestDTO.getName());
            throw new CustomException("Cannot add user "+createUserRequestDTO.getName()+" some roles are not correct", HttpStatus.BAD_REQUEST);
        }
    }

}
