package com.aberdote.OVPN4ALL.utils.converter;

import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class EntityConverterTest {


    private static final List<UserEntity> userEntities = List.of(new UserEntity("Paco", "Paco@gmail.com", "Paco"),
            new UserEntity("Fernando ternasco", "Fernando@gmail.com", "123"),
            new UserEntity("LaPlace", "fourier@gmail.com", "lp123"),
            new UserEntity("Lakir", "PLakir@gmail.com", "hashanb234"),
            new UserEntity("Samuel L jackson", "slj@gmail.com", "eskan123"),
            new UserEntity("Fakson", "hijodefak@gmail.com", "eskorpion78"),
            new UserEntity("toledo", "Acero@gmail.com", "69ydesobe"),
            new UserEntity("eksere", "Asere@gmail.com", "yndesuka"),
            new UserEntity("medella", "medallo@gmail.com", "123asd123asd"));

    @DisplayName("User entity to UserResponseDTO test")
    @Test
    public void fromUserEntityToUserResponseDTO() {
        userEntities.forEach(userEntity ->  {
            final UserResponseDTO userResponseDTO = EntityConverter.fromUserEntityToUserResponseDTO(userEntity);
            assertEquals(userEntity.getName(), userResponseDTO.getName());
            assertEquals(userEntity.getEmail(), userResponseDTO.getEmail());
            assertEquals(userEntity.getId(), userResponseDTO.getId());
            assertEquals(userEntity.getCreatedAt(), userResponseDTO.getCreatedAt());
        });
    }

}
