package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.integration.argument.provider.AdminUserProvider;
import com.aberdote.OVPN4ALL.integration.argument.provider.BadUserProvider;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.BASE_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.USER_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UserControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private final static Faker FAKER = new Faker();
    private static final CreateUserRequestDTO USER = new CreateUserRequestDTO(
            FAKER.name().firstName(),
            FAKER.internet().emailAddress(),
            FAKER.internet().password(),
            List.of(new RoleDTO(ROLE_ADMIN))
    );
    private String token;

    @BeforeEach
    void getToken() {
        userService.addUser(USER);
        token = loginUser(USER.getName(), USER.getPassword(), port);
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    @DisplayName("Test save user ok")
    @ParameterizedTest
    @ArgumentsSource(AdminUserProvider.class)
    void saveUser_ok(CreateUserRequestDTO newUser) {

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(JSON)
                .body(newUser)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH))
        .then()
                .statusCode(OK.value());

    }

    @DisplayName("Test save bad user")
    @ParameterizedTest
    @ArgumentsSource(BadUserProvider.class)
    void saveUser_bad(CreateUserRequestDTO newUser) {

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(JSON)
                .body(newUser)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH))
        .then()
                .statusCode(BAD_REQUEST.value());

    }

    @DisplayName("Test save user with no token")
    @Test
    void saveUser_no_token() {

        given()
                .request()
                .contentType(JSON)
                .body(USER)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test get users by page ok")
    @Test
    void getUsers_ok() {

        Stream
                .generate(FAKER::name)
                .map(Name::firstName)
                .distinct()
                .limit(20)
                .map(name ->
                        new CreateUserRequestDTO(
                                name,
                                FAKER.internet().emailAddress(),
                                FAKER.internet().password(),
                                generateRandomRoles(new RoleDTO(ROLE_ADMIN))
                        )
                ).forEach(userService::addUser);

        final JsonPath page =
                given()
                        .request()
                        .contentType(JSON)
                        .headers(buildAuthHeaders(token))
                        .params(Map.of("page", "0", "limit", "20"))
                .when()
                        .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH))
                .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .body()
                        .jsonPath();

        assertEquals(20, (Integer) page.get("currentUsers"));

    }

    @DisplayName("Test users no token")
    @Test
    void getUsers_no_token() {

        given()
                .request()
                .contentType(JSON)
                .params(Map.of("page", "0", "limit", "20"))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

}
