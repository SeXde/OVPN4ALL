package com.aberdote.OVPN4ALL.util.validator.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidatorTest {

    @ParameterizedTest @DisplayName("validateEmail test with valid emails")
    @ValueSource(strings = {"pacco@test.com", "pedro@domain.com", "far@lopa.ch", "muca@sica.kr", "paño@baño.es"})
    public void validateEmailWithValidEmails(String email) {
        assertTrue(UserValidator.validateEmail(email));
    }

    @ParameterizedTest @DisplayName("validateEmail test with non-valid emails")
    @ValueSource(strings = {"paccotest.com", "pedro@domain.com.las@", "", " ", "Select * from users;", "GimmiTest", "@asd", ".com@.com"})
    public void validateEmailWithInValidEmails(String email) {
        assertFalse(UserValidator.validateEmail(email));
    }

}
