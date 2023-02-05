package com.aberdote.OVPN4ALL.integration.utils;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.github.javafaker.Faker;
import io.restassured.path.json.JsonPath;
import org.springframework.http.HttpStatus;

import java.util.*;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.APPLICATION_JSON;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_USER;
import static io.restassured.RestAssured.given;

public final class IntegrationTestUtils {

    private IntegrationTestUtils() {}
    private static final Faker FAKER = new Faker();


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
                .headers(buildAuthHeaders(token))
        .when()
                .delete("http://localhost:".concat(String.valueOf(port)).concat("/api/users/name/").concat(name))
        .then()
                .statusCode(HttpStatus.OK.value());

    }

    public static Map<String, String> buildAuthHeaders(String token) {
        return Map.of("Authorization", "Bearer ".concat(token));
    }

}
