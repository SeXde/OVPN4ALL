package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.ErrorDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.util.Converter;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.util.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j @Service
@Transactional @RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    @Value("$(auth.cookie.hmac-key:secret.key)")
    private String secretKey;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;



    @Override
    public ErrorDTO addUser(CreateUserRequestDTO createUserRequestDTO) {
        final ErrorDTO errorDTO = new ErrorDTO(null);
        if (!UserValidator.validateEmail(createUserRequestDTO.getEmail())) {
            log.error("email {} is mot valid", createUserRequestDTO.getEmail());
            errorDTO.setError("email "+createUserRequestDTO.getEmail()+" is not valid");
            return errorDTO;
        }
        if (userRepository.findByNameIgnoreCase(createUserRequestDTO.getName()).isPresent()) {
            log.error("Cannot add user "+createUserRequestDTO.getName() + " already exists");
            errorDTO.setError("User "+createUserRequestDTO.getName()+" already exists");
        } else {
            log.info("Adding user "+createUserRequestDTO.getName());
            final UserEntity userEntity = Converter.convertFromDTOUser(createUserRequestDTO);
            userEntity.setPassword(this.cipher(userEntity.getPassword()));
            userRepository.save(userEntity);
        }
        return errorDTO;
    }

    @Override
    public ErrorDTO deleteUser(String userName) {
        return this.deleteUser(userRepository.findByNameIgnoreCase(userName));
    }

    @Override
    public ErrorDTO deleteUser(Long id) {
        return this.deleteUser(userRepository.findById(id));
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
    public ErrorDTO validateUser(LoginUserRequestDTO loginUserRequestDTO) {
        ErrorDTO errorDTO = new ErrorDTO("User or password is not valid");
        Optional<UserEntity> optUser = userRepository.findByNameIgnoreCase(loginUserRequestDTO.getName());
        if (optUser.isEmpty()){
            log.error("User "+loginUserRequestDTO.getName()+" does not exist");
            return errorDTO;
        }
        UserEntity user = optUser.get();
        if (!this.isAdmin(user) && !this.isOwner(user)){
            log.error("User "+user.getName()+" has not privileges");
            errorDTO.setError("User has no privileges");
        }
        else if (user.getPassword().equals(this.cipher(loginUserRequestDTO.getPassword()))){
            log.info("User " + user.getName()+" has been validated");
            errorDTO.setError(null);
        } else {
            log.error("Password is not correct for user "+user.getName());
        }
        return errorDTO;
    }

    @Override
    public ErrorDTO addRoleToUser(String sender, String receiver, String roleName) {
        final ErrorDTO errorDTO = new ErrorDTO(null);
        Optional<UserEntity> senderUserOpt = userRepository.findByNameIgnoreCase(sender);
        if (senderUserOpt.isEmpty()) {
            log.error("User "+sender+" does not exist");
            errorDTO.setError("User "+sender+" does not exist");
            return errorDTO;
        }
        UserEntity senderUser = senderUserOpt.get();
        if (!UserValidator.validateRole(senderUser, roleName)) {
            log.error("User is not allowed to add role "+roleName);
            errorDTO.setError("User is not allowed to add role "+roleName);
            return errorDTO;
        }
        return userRepository.findByNameIgnoreCase(receiver)
                .map(user -> {
                    Optional<RoleEntity> roleEntityOpt = roleRepository.findByRoleName(roleName);
                    if (roleEntityOpt.isEmpty()) {
                        log.error("Role "+roleName+" not found");
                        errorDTO.setError("Role "+roleName+" not found");
                        return errorDTO;
                    }
                    user.getRoles().add(roleEntityOpt.get());
                    log.info("User "+receiver+" has got new role: "+roleName);
                    return errorDTO;
                })
                .orElseGet(() -> {
                    log.error("User "+receiver+" not found");
                    errorDTO.setError("User "+receiver+" not found");
                    return errorDTO;
                });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNameIgnoreCase(username)
                .map((user) -> {
                    log.info("User {} found in the database", username);
                    return new org.springframework.security.core.userdetails.User(user.getName(),
                            user.getPassword(),
                            user.getRoles()
                                    .stream()
                                    .map(role -> new SimpleGrantedAuthority(role.getRoleName())).toList());
                })
                .orElseThrow(() -> {
                    log.error("User {} not found in the database", username);
                    throw new UsernameNotFoundException("User "+username+" not found");
                });
    }

    private String cipher(String username) {
        byte[] secretKeyBytes = Objects.requireNonNull(secretKey)
                .getBytes(StandardCharsets.UTF_8);
        byte [] valueBytes = username
                .getBytes(StandardCharsets.UTF_8);
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec sec = new SecretKeySpec(secretKeyBytes, "HmacSHA512");
            mac.init(sec);
            byte [] hmacBytes = mac.doFinal(valueBytes);
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAdmin(UserEntity userEntity) {
        return userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList()
                .contains(RoleConstants.ROLE_ADMIN);
    }

    private boolean isUser(UserEntity userEntity) {
        return userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList()
                .contains(RoleConstants.ROLE_USER);
    }

    private boolean isOwner(UserEntity userEntity) {
        return userEntity.getRoles()
                .stream()
                .map(RoleEntity::getRoleName)
                .toList()
                .contains(RoleConstants.ROLE_OWNER);
    }

    private boolean isLastOwner(UserEntity userEntity) {
        return  this.isOwner(userEntity)
                && userRepository.findAll()
                .stream()
                .filter(this::isOwner)
                .toList()
                .size() == 1;
    }

    private ErrorDTO deleteUser(Optional<UserEntity> optionalUserEntity) {
        final ErrorDTO errorDTO = new ErrorDTO(null);
        if (optionalUserEntity.isEmpty()) {
            log.error("Cannot find user to delete");
            errorDTO.setError("User not found");
            return errorDTO;
        }
        if (this.isLastOwner(optionalUserEntity.get())) {
            log.error("User is last Admin or Owner");
            errorDTO.setError("Cannot delete last Administrator");
            return errorDTO;
        }
        log.info("Deleting user "+optionalUserEntity.get().getName());
        userRepository.delete(optionalUserEntity.get());
        return errorDTO;
    }

    private UserResponseDTO getUser(Optional<UserEntity> optionalUserEntity) {
        return optionalUserEntity
            .map(user -> {
                log.info("Getting user "+optionalUserEntity.get().getName());
                return Converter.convertDTOUser(user);
            })
            .orElseGet(() -> {
                log.error("Cannot get user, it doesn't exists");
                return null;
            });
    }
}
