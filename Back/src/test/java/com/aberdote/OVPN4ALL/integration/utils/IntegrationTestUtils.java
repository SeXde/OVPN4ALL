package com.aberdote.OVPN4ALL.integration.utils;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.github.javafaker.Faker;
import io.restassured.path.json.JsonPath;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.APPLICATION_JSON;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_USER;
import static io.restassured.RestAssured.given;

public final class IntegrationTestUtils {

    private IntegrationTestUtils() {}

    public static Stream<CreateUserRequestDTO> provideUsers(RoleDTO roleDTO, Integer users, boolean notRandomRole) {

        final Faker faker = new Faker();
        return Stream.generate(() -> new CreateUserRequestDTO(
                faker.name().firstName(),
                faker.internet().emailAddress(),
                faker.internet().password(),
                notRandomRole ? List.of(roleDTO) : generateRandomRoles(roleDTO)
        )).limit(users);

    }
    public static List<RoleDTO> generateRandomRoles(RoleDTO role) {

        final Set<RoleDTO> roles = new HashSet<>();
        Optional.ofNullable(role).ifPresent(roles::add);
        roles.add(new RoleDTO(new Random().nextBoolean() ? ROLE_USER : ROLE_ADMIN ));
        return new ArrayList<>(roles);

    }

    public static String loginUser(String name, String password, Integer port) {

        final JsonPath token =
                given()
                    .request()
                    .contentType(APPLICATION_JSON)
                    .body(Map.of("name", name, "password", password))
                .when()
                    .post("http://localhost:".concat(String.valueOf(port)).concat("/api/users/signIn"))
                .then()
                    .statusCode(HttpStatus.ACCEPTED.value())
                    .extract()
                    .body()
                    .jsonPath();

        return token.get("token");

    }

    public static void deleteUser(String name, String token, Integer port) {

        given()
                .request()
                .headers(Map.of("Authorization", "Bearer ".concat(token)))
        .when()
                .delete("http://localhost:".concat(String.valueOf(port)).concat("/api/users/name/").concat(name))
        .then()
                .statusCode(HttpStatus.OK.value());

    }

}
