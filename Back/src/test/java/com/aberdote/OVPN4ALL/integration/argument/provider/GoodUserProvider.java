package com.aberdote.OVPN4ALL.integration.argument.provider;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.github.javafaker.Faker;

import java.util.List;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.generateRandomRoles;

public class GoodUserProvider {

    private static final Faker FAKER = new Faker();

    protected static Stream<CreateUserRequestDTO> provideUsers(RoleDTO roleDTO, Integer users, boolean notRandomRole) {

        return Stream.generate(() -> new CreateUserRequestDTO(
                FAKER.name().firstName(),
                FAKER.internet().emailAddress(),
                FAKER.internet().password(),
                notRandomRole ? List.of(roleDTO) : generateRandomRoles(roleDTO)
        )).limit(users);

    }

}
