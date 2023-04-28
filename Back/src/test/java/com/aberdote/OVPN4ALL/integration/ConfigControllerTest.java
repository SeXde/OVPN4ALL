package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Stream;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.BASE_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.CONFIG_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.buildAuthHeaders;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.loginUser;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ConfigControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConfigService configService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private static String token;
    private final static ObjectMapper MAPPER = new ObjectMapper();
    private final static Faker FAKER = new Faker();
    private SetupDTO SETUP;
    private static final CreateUserRequestDTO USER = new CreateUserRequestDTO(
            FAKER.name().firstName(),
            FAKER.internet().emailAddress(),
            FAKER.internet().password(),
            List.of(new RoleDTO(ROLE_ADMIN))
    );

    @BeforeEach
    void getToken() {
        userService.addUser(USER);
        token = loginUser(USER.getName(), USER.getPassword(), port);
        SETUP = new SetupDTO(
                "7777",
                "192.168.0.1",
                "255.255.255.0",
                "2.22.123.1"

        );
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
        configRepository.deleteAll();
    }

    @DisplayName("Test get config ok")
    @Test
    void getConfig_ok() throws JsonProcessingException {

        configRepository.save(EntityConverter.fromSetupDTOToConfigEntity(SETUP));

        final String setupDTORaw =
        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .asString();

        assertNotNull(setupDTORaw);
        assertFalse(setupDTORaw.isEmpty());
        final SetupDTO setupDTO = MAPPER.readValue(setupDTORaw, SetupDTO.class);
        assertEquals(SETUP.getPort(), setupDTO.getPort());
        assertEquals(SETUP.getGateway(), setupDTO.getGateway());

    }

    @DisplayName("Test get config wrong")
    @Test
    void getConfig_not_found() {

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @DisplayName("Test get config only one")
    @Test
    void getConfig_only_one() throws JsonProcessingException {

        Stream
                .generate(() -> new SetupDTO(
                        "777",
                        "192.168.0.1",
                        "255.255.255.0",
                        FAKER.internet().publicIpV4Address()
                ))
                .limit(10)
                .forEach(configService::setConfig);

        assertEquals(1, configRepository.findAll().size());

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.OK.value());

    }

    @DisplayName("Test get config no token")
    @Test
    void getConfig_no_token() {

        given()
                .request()
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

    @DisplayName("Test set config ok")
    @Test
    void setConfig_ok() {

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
                .body(SETUP)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.CREATED.value());

        assertEquals(1, configRepository.findAll().size());

    }

    @DisplayName("Test set config more than one time")
    @Test
    void setConfig_more_times() {

        configService.setConfig(SETUP);
        SETUP.setPort("234");
        setConfig_ok();

    }

    @DisplayName("Test set config ko")
    @Test
    void setConfig_ko() {

        SETUP.setPort("not a port");
        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
                .body(SETUP)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("Test set config no token")
    @Test
    void setConfig_no_token() {

        given()
                .request()
                .contentType(ContentType.JSON)
                .body(SETUP)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(CONFIG_PATH))
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

}
