package com.aberdote.OVPN4ALL.unit.utils.validator.user;

import com.aberdote.OVPN4ALL.utils.validator.user.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {

    @ParameterizedTest
    @DisplayName("validateEmail test with valid emails")
    @ValueSource(strings = {"pacco@test.com", "pedro@domain.com", "far@lopa.ch", "muca@sica.kr", "pano@bano.es"})
    public void validateEmailWithValidEmails(String email) {
        Assertions.assertTrue(UserValidator.validateEmail(email));
    }

    @ParameterizedTest @DisplayName("validateEmail test with non-valid emails")
    @ValueSource(strings = {"paccotest.com", "pedro@domain.com.las@", "", " ", "Select * from users;", "GimmiTest", "@asd", ".com@.com"})
    public void validateEmailWithInValidEmails(String email) {
        assertFalse(UserValidator.validateEmail(email));
    }

}
