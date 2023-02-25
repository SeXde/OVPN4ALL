package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.*;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.buildAuthHeaders;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.loginUser;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class LogControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private final static CreateUserRequestDTO USER = new CreateUserRequestDTO(
            "Paco",
            "Paco@gmail.com",
            "Paco1234!.",
            List.of(new RoleDTO(ROLE_ADMIN))
    );

    private static String token;

    @BeforeEach
    void getToken() {
        userService.addUser(USER);
        token = loginUser(USER.getName(), USER.getPassword(), port);
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    @DisplayName("Test download server logs")
    @Test
    void downloadLogs_ok() {

        given()
                .request()
                .headers(buildAuthHeaders(token))
                .contentType(APPLICATION_ZIP)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(LOG_PATH))
        .then()
                .statusCode(HttpStatus.OK.value());

    }

}
