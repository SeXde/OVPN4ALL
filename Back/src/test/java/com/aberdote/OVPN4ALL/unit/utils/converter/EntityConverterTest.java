package com.aberdote.OVPN4ALL.unit.utils.converter;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EntityConverterTest {

    @DisplayName("Test converter from UserEntity to UserResponseDTO")
    @ParameterizedTest
    @MethodSource("testUsersEntities")
    void fromUserEntityToUserResponseDTO(UserEntity userEntity) {
        final UserResponseDTO userResponseDTO = EntityConverter.fromUserEntityToUserResponseDTO(userEntity);
        assertEquals(userEntity.getName(), userResponseDTO.getName());
        assertEquals(userEntity.getEmail(), userResponseDTO.getEmail());
        assertEquals(userEntity.getId(), userResponseDTO.getId());
        assertEquals(userEntity.getCreatedAt(), userResponseDTO.getCreatedAt());
    }

    @DisplayName("Test converter from CreateUserRequestDTO to UserEntity")
    @ParameterizedTest
    @MethodSource("testCreateUsersRequestDTO")
    void fromCreateUserDTOToUserEntity(CreateUserRequestDTO createUserRequestDTO) {
        final UserEntity userEntity = EntityConverter.fromCreateUserDTOToUserEntity(createUserRequestDTO);
        assertEquals(createUserRequestDTO.getName(), userEntity.getName());
        assertEquals(createUserRequestDTO.getPassword(), userEntity.getPassword());
        assertEquals(createUserRequestDTO.getEmail(), userEntity.getEmail());
    }

    @DisplayName("Test converter from ConfigEntity to SetupDTO")
    @ParameterizedTest
    @MethodSource("testConfigEntities")
    void fromConfigEntityToSetupDTO(ConfigEntity configEntity) {
        final SetupDTO setupDTO = EntityConverter.fromConfigEntityToSetupDTO(configEntity);
        assertEquals(setupDTO.getPort(), configEntity.getPort());
        assertEquals(setupDTO.getSubnet(), configEntity.getNetmask());
        assertEquals(setupDTO.getServer(), configEntity.getServer());
        assertEquals(setupDTO.getGateway(), configEntity.getGateway());
    }

    static Stream<UserEntity> testUsersEntities() {
        return Stream.of(
                new UserEntity("Fernando ternasco", "Fernando@gmail.com", "123"),
                new UserEntity("LaPlace", "fourier@gmail.com", "lp123"),
                new UserEntity("Lakir", "PLakir@gmail.com", "hashanb234"),
                new UserEntity("Samuel L jackson", "slj@gmail.com", "eskan123"),
                new UserEntity("Fakson", "hijodefak@gmail.com", "eskorpion78"),
                new UserEntity("toledo", "Acero@gmail.com", "69ydesobe"),
                new UserEntity("eksere", "Asere@gmail.com", "yndesuka"),
                new UserEntity("medella", "medallo@gmail.com", "123asd123asd")
        );
    }

    static Stream<CreateUserRequestDTO> testCreateUsersRequestDTO() {
        return Stream.of(
                new CreateUserRequestDTO("Fernando ternasco", "Fernando@gmail.com", "123", List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("LaPlace", "fourier@gmail.com", "lp123", List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Lakir", "PLakir@gmail.com", "hashanb234", List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Samuel L jackson", "slj@gmail.com", "eskan123", List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Fakson", "hijodefak@gmail.com", "eskorpion78", List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("toledo", "Acero@gmail.com", "69ydesobe", List.of(new RoleDTO(RoleConstants.ROLE_USER), new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("eksere", "Asere@gmail.com", "yndesuka", List.of(new RoleDTO(RoleConstants.ROLE_USER), new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("medella", "medallo@gmail.com", "123asd123asd", List.of(new RoleDTO(RoleConstants.ROLE_USER), new RoleDTO(RoleConstants.ROLE_ADMIN)))
        );
    }

    static Stream<ConfigEntity> testConfigEntities() {
        return Stream.of(
                new ConfigEntity("1234", "192.168.4.1", "255.255.255.1", "230.132.2.1"),
                new ConfigEntity("3444", "192.168.8.1", "255.255.255.2", "230.131.2.1"),
                new ConfigEntity("8934", "192.168.41.1", "255.255.255.3", "230.130.2.1"),
                new ConfigEntity("12344", "192.168.0.1", "255.255.255.4", "230.131.2.1"),
                new ConfigEntity("15764", "192.163.9.1", "255.255.255.5", "230.132.2.1"),
                new ConfigEntity("89034", "192.118.4.1", "255.255.255.6", "230.143.2.1")
        );
    }


}
