package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.StatusService;
import com.aberdote.OVPN4ALL.service.UserService;
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

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.API_STATUS;
import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.BASE_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.buildAuthHeaders;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.loginUser;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class StatusControllerTest {


    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private StatusService statusService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConfigRepository configRepository;
    @LocalServerPort
    private int port;
    private final static Faker FAKER = new Faker();
    private static final CreateUserRequestDTO USER = new CreateUserRequestDTO(
            FAKER.name().firstName(),
            FAKER.internet().emailAddress(),
            FAKER.internet().password(),
            List.of(new RoleDTO(ROLE_ADMIN))
    );
    private static String token;

    @BeforeEach
    void setupTest() {

        userService.addUser(USER);
        token = loginUser(USER.getName(), USER.getPassword(), port);
        configService.setConfig(new SetupDTO(
                "7777",
                "192.168.0.1",
                "255.255.255.0",
                "2.22.123.1"

        ));

    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
        configRepository.deleteAll();
    }

    @DisplayName("Test server status on")
    @Test
    void getStatus_on() {

        statusService.turnOn();
        final String status =
        given()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS))
        .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .asString();

        assertEquals("true", status);
        statusService.turnOff();

    }

    @DisplayName("Test server status off")
    @Test
    void getStatus_off() {

        statusService.turnOff();
        final String status =
                given()
                        .headers(buildAuthHeaders(token))
                        .contentType(ContentType.JSON)
                .when()
                        .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS))
                .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract()
                        .body()
                        .asString();

        assertEquals("false", status);

    }

    @DisplayName("Test start vpn")
    @Test
    void startOpenvpn() {

        statusService.turnOff();
        given()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS).concat("/on"))
        .then()
                .statusCode(HttpStatus.OK.value());

        assertTrue(statusService.isActive());
        statusService.turnOff();

    }

    @DisplayName("Test shutdown vpn")
    @Test
    void shutdownOpenvpn() {

        statusService.turnOn();
        given()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS).concat("/off"))
        .then()
                .statusCode(HttpStatus.OK.value());

        assertFalse(statusService.isActive());

    }

    @DisplayName("Test get bandwidth on")
    @Test
    void getBandwidth() {

        statusService.turnOn();
        given()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS).concat("/bandwidth"))
        .then()
                .statusCode(HttpStatus.OK.value());
        statusService.turnOff();

    }

    @DisplayName("Test get bandwidth off")
    @Test
    void getBandwidth_off() {

        statusService.turnOff();
        given()
                .headers(buildAuthHeaders(token))
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(API_STATUS).concat("/bandwidth"))
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }


}
