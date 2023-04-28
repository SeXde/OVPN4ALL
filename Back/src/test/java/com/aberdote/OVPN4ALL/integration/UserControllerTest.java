package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.LoginUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.integration.argument.provider.AdminUserProvider;
import com.aberdote.OVPN4ALL.integration.argument.provider.BadUserProvider;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.*;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.*;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.*;
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
    @Autowired
    private ConfigRepository configRepository;
    private final static ObjectMapper MAPPER = new ObjectMapper();
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

    @DisplayName("Test get user by page no token")
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

    @DisplayName("Test get user ok")
    @Test
    void getUser_ok() throws IOException {

        final Long id =
                userRepository
                        .findAll()
                        .parallelStream()
                        .findAny()
                        .map(UserEntity::getId)
                        .orElse(-1L);

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat(id.toString()))
        .then()
                .statusCode(OK.value());

    }

    @DisplayName("Test get user not found")
    @Test
    void getUser_not_found() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("2345"))
        .then()
                .statusCode(NOT_FOUND.value());

    }

    @DisplayName("Test get user no token")
    @Test
    void getUser_no_token() {

        given()
                .contentType(JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("2345"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test login ok")
    @Test
    void login_ok() {

        final LoginUserRequestDTO body = new LoginUserRequestDTO(USER.getName(), USER.getPassword());

        given()
                .contentType(JSON)
                .body(body)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("signIn"))
        .then()
                .statusCode(ACCEPTED.value());

    }

    @DisplayName("Test login user ko")
    @Test
    void login_ko() {

        final LoginUserRequestDTO body = new LoginUserRequestDTO(USER.getName(), "Caserio2");

        given()
                .contentType(JSON)
                .body(body)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("signIn"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test delete user not found")
    @Test
    void deleteUser_not_found() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .delete(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("2345"))
        .then()
                .statusCode(NOT_FOUND.value());

    }

    @DisplayName("Test delete user no token")
    @Test
    void deleteUser_no_token() {

        given()
                .contentType(JSON)
        .when()
                .delete(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("2345"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test delete user last admin")
    @Test
    void deleteUserWithName_last_admin() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .delete(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("name/").concat(USER.getName()))
        .then()
                .statusCode(FORBIDDEN.value());

    }

    @DisplayName("Test token ok")
    @Test
    void testToken_ok() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("token"))
        .then()
                .statusCode(OK.value());

    }

    @DisplayName("Test token ko")
    @Test
    void testToken_ko() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders("klk"))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("token"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test download file ok")
    @Test
    void downloadFile_ok() {

        configRepository.save(new ConfigEntity("444", "192.168.0.1", "255.255.255.0", "21.1.2.3"));

        final Long id =
                userRepository
                        .findAll()
                        .parallelStream()
                        .findAny()
                        .map(UserEntity::getId)
                        .orElse(-1L);

        final String body =
        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat(id.toString()).concat("/ovpn"))
        .then()
                .statusCode(OK.value())
                .extract()
                .body()
                .asString();

        assertNotNull(body);
        assertFalse(body.isEmpty());
        assertTrue(body.contains("21.1.2.3"));

        configRepository.deleteAll();

    }

    @DisplayName("Test download file no setup")
    @Test
    void downloadFile_no_setup() {

        final Long id =
                userRepository
                        .findAll()
                        .parallelStream()
                        .findAny()
                        .map(UserEntity::getId)
                        .orElse(-1L);

                given()
                        .contentType(JSON)
                        .headers(buildAuthHeaders(token))
                .when()
                        .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat(id.toString()).concat("/ovpn"))
                .then()
                        .statusCode(NOT_FOUND.value());

    }

    @DisplayName("Test download file no token")
    @Test
    void downloadFile_no_token() {

        given()
                .contentType(JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("213").concat("/ovpn"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test disconnect user ok")
    @Test
    void disconnectUser_ok() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("disconnect/").concat(USER.getName()))
        .then()
                .statusCode(OK.value());

    }

    @DisplayName("Test disconnect user not found")
    @Test
    void disconnectUser_not_found() {

        given()
                .contentType(JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("disconnect/test"))
        .then()
                .statusCode(NOT_FOUND.value());

    }

    @DisplayName("Test disconnect user no token")
    @Test
    void disconnectUser_no_token() {

        given()
                .contentType(JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(USER_PATH).concat("disconnect/test"))
        .then()
                .statusCode(UNAUTHORIZED.value());

    }

    @DisplayName("Test No users true")
    @Test
    void noUser_true() {

        userRepository.deleteAll();
        final String result =
                given()
                        .request()
                        .contentType(APPLICATION_JSON)
                .when()
                        .get("http://localhost:".concat(String.valueOf(port)).concat(USER_PATH).concat("noUsers"))
                .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .body()
                        .asString();
        assertEquals("true", result);

    }

    @DisplayName("Test No users false")
    @Test
    void noUser_false() {

        final String result =
                given()
                        .request()
                        .contentType(APPLICATION_JSON)
                .when()
                        .get("http://localhost:".concat(String.valueOf(port)).concat(USER_PATH).concat("noUsers"))
                .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .body()
                        .asString();
        assertEquals("false", result);

        userRepository.deleteAll();

    }

}
