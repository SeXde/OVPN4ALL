package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.common.constanst.UserReservedConstants;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.aberdote.OVPN4ALL.TestUtils.testException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  RoleRepository roleRepository;
    @Mock
    private  PasswordEncoder bCryptPasswordEncoder;
    @Mock
    private  CommandService commandService;
    @Mock
    private  ConfigService configService;
    private UserService userService;

    private CreateUserRequestDTO createUserRequestDTO;

    @BeforeEach
    void initUserService() {
        userService = new UserServiceImpl(userRepository, roleRepository, bCryptPasswordEncoder, commandService,
                configService);
        createUserRequestDTO = new CreateUserRequestDTO("Paco", "Paco@hotmail.com", "Paco123",
                List.of(
                        new RoleDTO(RoleConstants.ROLE_USER),
                        new RoleDTO(RoleConstants.ROLE_ADMIN)
                )
        );
    }

    @DisplayName("Tes add user with correct data")
    @Test
    void addUserOk() throws IOException, InterruptedException {
        when(commandService.addUser(anyString(), anyString())).thenReturn(Boolean.TRUE);
        createUserRequestDTO.getRoles()
                .stream()
                .map(RoleDTO::getRoleName)
                .forEach(roleName ->
                        when(roleRepository.findByRoleName(eq(roleName))).thenReturn(Optional.of(new RoleEntity(roleName)))
                );
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("test");
        when(userRepository.save(any(UserEntity.class))).then(returnsFirstArg());
        final UserResponseDTO userResponseDTO = userService.addUser(createUserRequestDTO);
        assertEquals(createUserRequestDTO.getName(), userResponseDTO.getName());
        assertEquals(createUserRequestDTO.getEmail(), userResponseDTO.getEmail());
        assertEquals(createUserRequestDTO.getRoles().size(), userResponseDTO.getRoles().size());
        assertTrue(IntStream.range(0,
                createUserRequestDTO
                        .getRoles()
                        .size())
                .allMatch(i ->
                        createUserRequestDTO
                                .getRoles()
                                .stream()
                                .toList()
                                .get(i)
                                .getRoleName()
                                .equals(userResponseDTO.getRoles().stream().toList().get(i).getRoleName()))
        );
        verify(commandService, times(1)).addUser(anyString(), anyString());
        verify(roleRepository, times(2)).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @DisplayName("Test add user with null user")
    @Test
    void addUserNull() throws IOException, InterruptedException {
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(null)
        );
        assertEquals("User is null", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, never()).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, never()).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with invalid password")
    @Test
    void addUserInvalidPassword() throws IOException, InterruptedException {
        final String forbiddenPassword = UserReservedConstants.PASSWORD_FORBIDDEN
                .stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
        createUserRequestDTO.setPassword(forbiddenPassword);
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals(String.format("Password %s is not valid", forbiddenPassword), customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, never()).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, never()).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with invalid email")
    @Test
    void addUserInvalidEmail() throws IOException, InterruptedException {
        createUserRequestDTO.setEmail("test@");
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals("Email test@ is not valid", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, never()).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, never()).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with duplicated user")
    @Test
    void addUserDuplicatedUser() throws IOException, InterruptedException {
        when(userRepository.findByNameIgnoreCase(createUserRequestDTO.getName())).thenReturn(Optional.of(new UserEntity()));
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals(String.format("User %s is already registered", createUserRequestDTO.getName()), customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, never()).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with invalid role")
    @Test
    void addUserRoleNotFound() throws IOException, InterruptedException {
        createUserRequestDTO.setRoles(List.of(new RoleDTO(RoleConstants.ROLE_USER),
                new RoleDTO("test"),
                new RoleDTO(RoleConstants.ROLE_ADMIN))
        );
        when(commandService.addUser(anyString(), anyString())).thenReturn(Boolean.TRUE);
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(new RoleEntity()));
        when(roleRepository.findByRoleName(eq("test"))).thenReturn(Optional.empty());
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals(String.format("Cannot add user %s, role test doesn't exist", createUserRequestDTO.getName()), customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, times(1)).addUser(anyString(), anyString());
        verify(roleRepository, times(2)).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with command exception")
    @Test
    void addUserCommandException() throws IOException, InterruptedException {
        when(commandService.addUser(anyString(), anyString())).thenReturn(Boolean.FALSE);
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals(String.format("Cannot add user '%s', execution failed, see logs for more details", createUserRequestDTO.getName()),
                customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(commandService, times(1)).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with invalid command execution")
    @Test
    void addUserCommandKO() throws IOException, InterruptedException {
        when(commandService.addUser(anyString(), anyString())).thenThrow(new InterruptedException("test"));
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals("Cannot execute add user script, ErrorMessage: 'test'",
                customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(commandService, times(1)).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test add user with empty roles")
    @Test
    void addUserEmptyRoles() throws IOException, InterruptedException {
        createUserRequestDTO.setRoles(Collections.emptyList());
        final CustomException customException = assertThrows(CustomException.class, () ->
                userService.addUser(createUserRequestDTO)
        );
        assertEquals("Roles cannot be empty or null",
                customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(commandService, never()).addUser(anyString(), anyString());
        verify(roleRepository, never()).findByRoleName(anyString());
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
    }

    @DisplayName("Test validate user with correct data")
    @Test
    void validateUserOk() {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        userEntity.setRoles(createUserRequestDTO.getRoles().stream().map(EntityConverter::fromRoleDTOtoRoleEntity).collect(Collectors.toSet()));
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(Boolean.TRUE);
        userService.validateUser(new LoginUserRequestDTO(createUserRequestDTO.getName(), createUserRequestDTO.getPassword()));
        verify(userRepository, times(1)).findByNameIgnoreCase(eq(createUserRequestDTO.getName()));
        verify(bCryptPasswordEncoder, times(1)).matches(any(CharSequence.class), anyString());
    }

    @DisplayName("Test validate user with user null")
    @Test
    void validateUserNull() {
        final CustomException customException =
                assertThrows(CustomException.class, () -> userService.validateUser(null));
        assertEquals("User cannot be null", customException.getError());
        assertEquals(HttpStatus.BAD_REQUEST, customException.getHttpStatus());
        verify(userRepository, never()).findByNameIgnoreCase(anyString());
        verify(bCryptPasswordEncoder, never()).matches(any(CharSequence.class), anyString());
    }

    @DisplayName("Test validate user with invalid user")
    @Test
    void validateUserNotFound() {
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        final CustomException customException =
                assertThrows(CustomException.class, () ->
                        userService.validateUser(new LoginUserRequestDTO(createUserRequestDTO.getName(),
                                createUserRequestDTO.getPassword()))
                );
        assertEquals(String.format("User %s does not exist", createUserRequestDTO.getName()), customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(bCryptPasswordEncoder, never()).matches(any(CharSequence.class), anyString());
    }

    @DisplayName("Test validate user with user not admin")
    @Test
    void validateUserNotAdmin() {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        userEntity.setRoles(Set.of(new RoleEntity(RoleConstants.ROLE_USER)));
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        final CustomException customException =
                assertThrows(CustomException.class, () ->
                        userService.validateUser(new LoginUserRequestDTO(createUserRequestDTO.getName(),
                                createUserRequestDTO.getPassword()))
                );
        assertEquals(String.format("User %s has not privileges", createUserRequestDTO.getName()), customException.getError());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(bCryptPasswordEncoder, never()).matches(any(CharSequence.class), anyString());
    }

    @DisplayName("Test validate user with wrong password")
    @Test
    void validateUserWrongPassword() {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        userEntity.setRoles(createUserRequestDTO.getRoles().stream().map(EntityConverter::fromRoleDTOtoRoleEntity).collect(Collectors.toSet()));
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        final CustomException customException =
                assertThrows(CustomException.class, () ->
                        userService.validateUser(new LoginUserRequestDTO(createUserRequestDTO.getName(),
                                createUserRequestDTO.getPassword()))
                );
        assertEquals(String.format("Password is not correct for user %s", createUserRequestDTO.getName()), customException.getError());
        assertEquals(HttpStatus.UNAUTHORIZED, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(bCryptPasswordEncoder, times(1)).matches(any(CharSequence.class), eq(userEntity.getPassword()));
    }

    @DisplayName("Test add role to user with correct data")
    @Test
    void addRoleToUserOk() {
        final String sender = "Paco";
        final String receiver = "Pedro";
        final String roleName = RoleConstants.ROLE_USER;
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        final Set<RoleEntity> roleSet = new HashSet<>();
        roleSet.add(new RoleEntity(RoleConstants.ROLE_ADMIN));
        userEntity.setRoles(roleSet);
        when(userRepository.findByNameIgnoreCase(eq(sender))).thenReturn(Optional.of(userEntity));
        when(userRepository.findByNameIgnoreCase(eq(receiver))).thenReturn(Optional.of(userEntity));
        when(roleRepository.findByRoleName(eq(roleName))).thenReturn(Optional.of(new RoleEntity(roleName)));
        final UserResponseDTO userResponseDTO = userService.addRoleToUser(sender, receiver, roleName);
        assertEquals(userEntity.getName(), userResponseDTO.getName());
        assertEquals(userEntity.getEmail(), userResponseDTO.getEmail());
        assertEquals(2, userResponseDTO.getRoles().size());
        verify(userRepository, times(2)).findByNameIgnoreCase(anyString());
        verify(roleRepository, times(1)).findByRoleName(eq(roleName));
    }

    @DisplayName("Test add role to user with sender not found")
    @Test
    void addRoleToUserSenderNotFound() {
        final String sender = "Paco";
        final String receiver = "Pedro";
        final String roleName = RoleConstants.ROLE_USER;
        when(userRepository.findByNameIgnoreCase(eq(sender))).thenReturn(Optional.empty());
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.addRoleToUser(sender, receiver, roleName));
        assertEquals("User Paco does not exist", customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(roleRepository, never()).findByRoleName(eq(roleName));
    }

    @DisplayName("Test add role to user with receiver not found")
    @Test
    void addRoleToUserReceiverNotFound() {
        final String sender = "Paco";
        final String receiver = "Pedro";
        final String roleName = RoleConstants.ROLE_USER;
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        final Set<RoleEntity> roleSet = new HashSet<>();
        roleSet.add(new RoleEntity(RoleConstants.ROLE_ADMIN));
        userEntity.setRoles(roleSet);
        when(userRepository.findByNameIgnoreCase(eq(sender))).thenReturn(Optional.of(userEntity));
        when(userRepository.findByNameIgnoreCase(eq(receiver))).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(eq(roleName))).thenReturn(Optional.of(new RoleEntity()));
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.addRoleToUser(sender, receiver, roleName));
        assertEquals("User Pedro does not exist", customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(2)).findByNameIgnoreCase(anyString());
        verify(roleRepository, times(1)).findByRoleName(eq(roleName));
    }

    @DisplayName("Test add role to user with invalid roleName")
    @Test
    void addRoleToUserRoleNameNotFound() {
        final String sender = "Paco";
        final String receiver = "Pedro";
        final String roleName = "test";
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        final Set<RoleEntity> roleSet = new HashSet<>();
        roleSet.add(new RoleEntity(RoleConstants.ROLE_ADMIN));
        userEntity.setRoles(roleSet);
        when(roleRepository.findByRoleName(eq(roleName))).thenReturn(Optional.empty());
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.addRoleToUser(sender, receiver, roleName));
        assertEquals("Role test not found", customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(roleRepository, times(1)).findByRoleName(eq(roleName));
    }

    @DisplayName("Test add role to user with user not authorized")
    @Test
    void addRoleToUserSenderNotAuthorized() {
        final String sender = "Paco";
        final String receiver = "Pedro";
        final String roleName = RoleConstants.ROLE_ADMIN;
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        final Set<RoleEntity> roleSet = new HashSet<>();
        roleSet.add(new RoleEntity(RoleConstants.ROLE_USER));
        userEntity.setRoles(roleSet);
        when(roleRepository.findByRoleName(eq(RoleConstants.ROLE_ADMIN))).thenReturn(Optional.of(new RoleEntity()));
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.addRoleToUser(sender, receiver, roleName));
        assertEquals(String.format("User %s is not allowed to add role %s", sender, roleName), customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(roleRepository, times(1)).findByRoleName(eq(roleName));
    }

    @DisplayName("Test download vpn with correct data")
    @Test
    void downloadUserVPNOk() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(configService.getConfig()).thenReturn(
                new SetupDTO("213", "10.0.0.1", "255.255.255.0", "200.1.224.4")
        );
        when(commandService.downloadOVPNFile(anyString(), any(SetupDTO.class))).thenReturn(Files.newTemporaryFile());
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        userService.downloadUserVPN("Paco");
        verify(userRepository, times(1)).findByNameIgnoreCase(eq("Paco"));
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).downloadOVPNFile(eq("Paco"), any(SetupDTO.class));
    }

    @DisplayName("Test download vpn with user not found")
    @Test
    void downloadUserVPNUserNotFound() throws IOException, InterruptedException {
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        final CustomException customException = assertThrows(CustomException.class, () -> userService.downloadUserVPN(anyString()));
        assertEquals("Cannot find user to download vpn", customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(configService, never()).getConfig();
        verify(commandService, never()).downloadOVPNFile(anyString(), any(SetupDTO.class));
    }

    @DisplayName("Test download vpn with command exception")
    @Test
    void downloadUserVPNCommandException() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        when(commandService.downloadOVPNFile(anyString(), any(SetupDTO.class))).thenThrow(new IOException("test"));
        when(configService.getConfig()).thenReturn(
                new SetupDTO("213", "10.0.0.1", "255.255.255.0", "200.1.224.4")
        );
        final CustomException customException = assertThrows(CustomException.class, () -> userService.downloadUserVPN(anyString()));
        assertEquals("Cannot download config file, ErrorMessage: test", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).downloadOVPNFile(anyString(), any(SetupDTO.class));
    }

    @DisplayName("Test download vpn with invalid file")
    @Test
    void downloadUserVPNCommandInvalidFile() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(userEntity));
        when(configService.getConfig()).thenReturn(
                new SetupDTO("213", "10.0.0.1", "255.255.255.0", "200.1.224.4")
        );
        when(commandService.downloadOVPNFile(anyString(), any(SetupDTO.class))).thenReturn(null);
        final CustomException customException = assertThrows(CustomException.class, () -> userService.downloadUserVPN(anyString()));
        assertEquals("Cannot create user config, for more details check logs", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase(anyString());
        verify(configService, times(1)).getConfig();
        verify(commandService, times(1)).downloadOVPNFile(anyString(), any(SetupDTO.class));
    }

    @DisplayName("Test delete user with user not found")
    @Test
    void deleteUserNotFound() throws IOException, InterruptedException {
        when(userRepository.findByNameIgnoreCase("Paco")).thenReturn(Optional.empty());
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.deleteUser("Paco")
        );
        assertEquals("Cannot find user to delete", customException.getError());
        assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase("Paco");
        verify(commandService, never()).deleteUser(anyString());
        verify(commandService, never()).isActive();
        verify(commandService, never()).startUp();
        verify(commandService, never()).shutdown();
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @DisplayName("Test delete user with user last admin")
    @Test
    void deleteUserLastAdmin() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        userEntity.setRoles(Set.of(new RoleEntity(RoleConstants.ROLE_ADMIN)));
        when(userRepository.findAll()).thenReturn(List.of(userEntity));
        when(userRepository.findByNameIgnoreCase("Paco")).thenReturn(Optional.of(userEntity));
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.deleteUser("Paco")
        );
        assertEquals("User is last Admin", customException.getError());
        assertEquals(HttpStatus.FORBIDDEN, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase("Paco");
        verify(commandService, never()).deleteUser(anyString());
        verify(commandService, never()).isActive();
        verify(commandService, never()).startUp();
        verify(commandService, never()).shutdown();
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @DisplayName("Test delete user with command exception")
    @Test
    void deleteUserCommandException() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(userRepository.findByNameIgnoreCase("Paco")).thenReturn(Optional.of(userEntity));
        when(commandService.deleteUser(anyString())).thenThrow(new IOException("test"));
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.deleteUser("Paco")
        );
        assertEquals("Cannot execute delete user script, ErrorMessage: 'test'", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase("Paco");
        verify(commandService, times(1)).deleteUser(anyString());
        verify(commandService, times(1)).isActive();
        verify(commandService, never()).startUp();
        verify(commandService, never()).shutdown();
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @DisplayName("Test delete user with command bad execution")
    @Test
    void deleteUserCommandWrong() throws IOException, InterruptedException {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(userRepository.findByNameIgnoreCase("Paco")).thenReturn(Optional.of(userEntity));
        when(commandService.deleteUser(anyString())).thenReturn(Boolean.FALSE);
        final CustomException customException = assertThrows(CustomException.class,
                () -> userService.deleteUser("Paco")
        );
        assertEquals("Cannot delete user 'Paco', execution failed, see logs for more details", customException.getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, customException.getHttpStatus());
        verify(userRepository, times(1)).findByNameIgnoreCase("Paco");
        verify(commandService, times(1)).deleteUser(anyString());
        verify(commandService, times(1)).isActive();
        verify(commandService, never()).startUp();
        verify(commandService, never()).shutdown();
        verify(userRepository, never()).delete(any(UserEntity.class));
    }

    @DisplayName("Test register first user not admin")
    @Test
    void registerFirstUser_not_admin() {
        createUserRequestDTO.setRoles(createUserRequestDTO
                .getRoles()
                .parallelStream()
                .filter(role -> !Objects.equals(role.getRoleName(), RoleConstants.ROLE_ADMIN))
                .collect(Collectors.toList())
        );
        testException(HttpStatus.BAD_REQUEST, "is not admin", () -> userService.registerFirstUser(createUserRequestDTO));
    }

    @DisplayName("Test register first user not first user")
    @Test
    void registerFirstUser_not_first_user() {
        when(userRepository.findAll()).thenReturn(List.of(EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO)));
        testException(HttpStatus.BAD_REQUEST, "is not first user", () -> userService.registerFirstUser(createUserRequestDTO));
    }

    @DisplayName("test register first user ok")
    @Test
    void registerFirstUser_ok() throws IOException, InterruptedException {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        createUserRequestDTO.setRoles(List.of(new RoleDTO(RoleConstants.ROLE_ADMIN)));
        when(userRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(commandService.addUser(anyString(), anyString())).thenReturn(true);
        when(roleRepository.findByRoleName(anyString())).thenReturn(Optional.of(new RoleEntity(RoleConstants.ROLE_ADMIN)));
        final var userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        assertNotNull(userService.registerFirstUser(createUserRequestDTO));
    }
}
