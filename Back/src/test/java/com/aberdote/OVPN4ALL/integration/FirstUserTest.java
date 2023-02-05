package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.UserEntity;
import com.aberdote.OVPN4ALL.integration.argument.provider.AdminUserProvider;
import com.aberdote.OVPN4ALL.integration.argument.provider.RegularUserProvider;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.APPLICATION_JSON;
import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.USER_PATH;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class FirstUserTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;


    @DisplayName("Test register first user ok")
    @ParameterizedTest
    @ArgumentsSource(AdminUserProvider.class)
    void registerFirstUserTest_ok(CreateUserRequestDTO newUser) {

        testFirstUser(newUser, HttpStatus.OK);

    }

    @DisplayName("Test register first user not admin")
    @ParameterizedTest
    @ArgumentsSource(RegularUserProvider.class)
    void registerFirstUserTest_not_admin(CreateUserRequestDTO newUser) {

        testFirstUser(newUser, HttpStatus.BAD_REQUEST);

    }

    @DisplayName("Test register first user not first")
    @ParameterizedTest
    @ArgumentsSource(AdminUserProvider.class)
    void registerFirstUserTest_not_first(CreateUserRequestDTO newUser) {

        userRepository.save(new UserEntity("test", "test@gmail.com", "test"));
        testFirstUser(newUser, HttpStatus.BAD_REQUEST);

    }

    private void testFirstUser(CreateUserRequestDTO newUser, HttpStatus httpStatus) {

        given()
                .request()
                .contentType(APPLICATION_JSON)
                .body(newUser)
        .when()
                .post("http://localhost:".concat(String.valueOf(port)).concat(USER_PATH).concat("firstUser"))
        .then()
                .statusCode(httpStatus.value());

        userRepository.deleteAll();

    }





}
