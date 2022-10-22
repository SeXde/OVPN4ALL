package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.Converter;
import com.aberdote.OVPN4ALL.utils.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Override
    public UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO) {
        if (!UserValidator.validateEmail(createUserRequestDTO.getEmail())) {
            log.error("email {} is mot valid", createUserRequestDTO.getEmail());
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
        log.info("Adding user {}", createUserRequestDTO.getName());
        final UserEntity userEntity = Converter.convertFromDTOUser(createUserRequestDTO);
        userEntity.setRoles(createUserRequestDTO.getRoles().stream()
                .map(role -> roleRepository.findByRoleName(role.getRoleName())
                        .orElseThrow(() -> {
                            log.error("Cannot add user {}, role {} doesn't exist", createUserRequestDTO.getName(), role.getRoleName());
                            throw new CustomException("Cannot add user "+createUserRequestDTO.getName()+" role "+role.getRoleName()+" doesn't exist", HttpStatus.BAD_REQUEST);
                        }))
                .collect(Collectors.toSet())
        );
        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        return Converter.convertDTOUser(userRepository.save(userEntity));
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
        return userRepository.findAll().stream().map(Converter::convertDTOUser).toList();
    }

    @Override
    public Page<UserResponseDTO> getUsersPaginated(int pageNumber, int usersPerPage) {
        Page<UserEntity> all = userRepository.findAll(PageRequest.of(pageNumber, usersPerPage));
        Page<UserResponseDTO> map = all.map(Converter::convertDTOUser);
        List<UserResponseDTO> userResponseDTOS = map.toList();
        return userRepository.findAll(PageRequest.of(pageNumber, usersPerPage)).map(Converter::convertDTOUser);
    }

    @Override
    public void validateUser(LoginUserRequestDTO loginUserRequestDTO) {
        Optional<UserEntity> optUser = userRepository.findByNameIgnoreCase(loginUserRequestDTO.getName());
        if (optUser.isEmpty()){
            log.error("User {} does not exist", loginUserRequestDTO.getName());
            throw new CustomException("User "+loginUserRequestDTO.getName()+" does not exist", HttpStatus.NOT_FOUND);
        }
        UserEntity user = optUser.get();
        if (!this.isAdmin(user) && !this.isOwner(user)){
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
                    return Converter.convertDTOUser(user);
                })
                .orElseGet(() -> {
                    log.error("User {} not found", receiver);
                    throw new CustomException("User "+receiver+" not found", HttpStatus.NOT_FOUND);
                });
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

    private boolean isOwner(UserEntity userEntity) {
        return isRole(userEntity, RoleConstants.ROLE_OWNER);
    }

    private boolean isLastOwner(UserEntity userEntity) {
        return  this.isOwner(userEntity)
                && userRepository.findAll()
                .stream()
                .filter(this::isOwner)
                .toList()
                .size() == 1;
    }

    private void deleteUser(Optional<UserEntity> optionalUserEntity) {
        if (optionalUserEntity.isEmpty()) {
            log.error("Cannot find user to delete");
            throw new CustomException("Cannot find user to delete", HttpStatus.NOT_FOUND);
        }
        if (this.isLastOwner(optionalUserEntity.get())) {
            log.error("User is last Admin or Owner");
            throw new CustomException("User is last Admin or Owner", HttpStatus.FORBIDDEN);
        }
        log.info("Deleting user "+optionalUserEntity.get().getName());
        userRepository.delete(optionalUserEntity.get());
    }

    private UserResponseDTO getUser(Optional<UserEntity> optionalUserEntity) {
        return optionalUserEntity
            .map(user -> {
                log.info("Getting user {}", optionalUserEntity.get().getName());
                return Converter.convertDTOUser(user);
            })
            .orElseThrow(() -> new CustomException("Cannot get user, it doesn't exists", HttpStatus.NOT_FOUND));
    }
}
