package com.aberdote.OVPN4ALL.integration;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import com.aberdote.OVPN4ALL.repository.ConfigRepository;
import com.aberdote.OVPN4ALL.repository.MailRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import com.github.javafaker.Faker;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.List;

import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.BASE_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.ApiConstants.MAIL_PATH;
import static com.aberdote.OVPN4ALL.common.constanst.RoleConstants.ROLE_ADMIN;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.buildAuthHeaders;
import static com.aberdote.OVPN4ALL.integration.utils.IntegrationTestUtils.loginUser;
import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class MailControllerTest {

    @LocalServerPort
    private int port;

    @Value("${mail.smtp}")
    private String smtp;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.password}")
    private String password;
    private String token;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private ConfigRepository configRepository;
    private final static Faker FAKER = new Faker();
    private static final CreateUserRequestDTO USER = new CreateUserRequestDTO(
            FAKER.name().firstName(),
            FAKER.internet().emailAddress(),
            FAKER.internet().password(),
            List.of(new RoleDTO(ROLE_ADMIN))
    );

    private MailRequestDTO mailRequestDTO;

    @BeforeEach
    void getToken() {
        userService.addUser(USER);
        token = loginUser(USER.getName(), USER.getPassword(), port);
        mailRequestDTO = new MailRequestDTO(smtp, "25", false, username, password);
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    @DisplayName("Test set mail ok")
    @Test
    void setMail_ok() {

        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.OK.value());


    }


    @DisplayName("Test set mail bad password")
    @Test
    void setMail_bad_password() {

        mailRequestDTO.setPassword("bad");
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());


    }

    @DisplayName("Test set mail bad user")
    @Test
    void setMail_bad_user() {

        mailRequestDTO.setUsername("paco@keyba.com");
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    @DisplayName("Test set mail malformed user")
    @Test
    void setMail_malformed_user() {

        mailRequestDTO.setUsername("paco");
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("Test set mail malformed smtp")
    @Test
    void setMail_malformed_smtp() {

        mailRequestDTO.setSmtpHost("esemetepe");
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("Test set mail malformed port")
    @Test
    void setMail_malformed_port() {

        mailRequestDTO.setSmtpPort("purt");
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
                .body(mailRequestDTO)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());

    }

    @DisplayName("Test set mail no token")
    @Test
    void setMail_no_token() {

        given()
                .contentType(ContentType.JSON)
        .when()
                .post(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH))
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

    @DisplayName("Test send mail ok")
    @Test
    void sendMail_ok() {

        final String path = String.format("/%s/file/%s", mailRequestDTO.getUsername(), USER.getName());
        mailRepository.save(EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO));
        configRepository.save(new ConfigEntity("444", "192.168.0.1", "255.255.255.0", "21.2.14.2"));
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH).concat(path))
        .then()
                .statusCode(HttpStatus.OK.value());
        mailRepository.deleteAll();
        configRepository.deleteAll();

    }

    @DisplayName("Test send mail no config")
    @Test
    void sendMail_no_config() {

        final String path = String.format("/%s/file/%s", mailRequestDTO.getUsername(), USER.getName());
        mailRepository.save(EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO));
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH).concat(path))
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        mailRepository.deleteAll();
    }

    @DisplayName("Test send mail no mail")
    @Test
    void sendMail_no_mail() {


        final String path = String.format("/%s/file/%s", mailRequestDTO.getUsername(), USER.getName());
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH).concat(path))
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    @DisplayName("Test send mail no user")
    @Test
    void sendMail_no_user() {

        final String path = String.format("/%s/file/trest", mailRequestDTO.getUsername());
        mailRepository.save(EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO));
        configRepository.save(new ConfigEntity("444", "192.168.0.1", "255.255.255.0", "21.2.14.2"));
        given()
                .contentType(ContentType.JSON)
                .headers(buildAuthHeaders(token))
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH).concat(path))
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
        mailRepository.deleteAll();
        configRepository.deleteAll();

    }

    @DisplayName("Test send mail no token")
    @Test
    void sendMail_no_token() {

        final String path = String.format("/%s/file/trest", mailRequestDTO.getUsername());
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(BASE_PATH.concat(String.valueOf(port)).concat(MAIL_PATH).concat(path))
        .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

    }

}
