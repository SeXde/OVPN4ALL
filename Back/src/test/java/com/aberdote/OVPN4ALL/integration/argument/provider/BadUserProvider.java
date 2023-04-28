package com.aberdote.OVPN4ALL.integration.argument.provider;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.Collections;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.generateRandomRoles;

public class BadUserProvider implements ArgumentsProvider {

    private final static Faker FAKER = new Faker();

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return provideBadUsers()
                .map(Arguments::of);
    }

    private static Stream<CreateUserRequestDTO> provideBadUsers() {

        return Stream.of(
                new CreateUserRequestDTO(
                        "",
                        FAKER.internet().emailAddress(),
                        FAKER.internet().password(),
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        "",
                        FAKER.internet().password(),
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        "'",
                        FAKER.internet().password(),
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        "testemail@asd",
                        FAKER.internet().password(),
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        FAKER.internet().emailAddress(),
                        "",
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        FAKER.internet().emailAddress(),
                        "'",
                        generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                ),
                new CreateUserRequestDTO(
                        FAKER.name().firstName(),
                        FAKER.internet().emailAddress(),
                        FAKER.internet().password(),
                        Collections.emptyList()
                )
        );

    }

}
