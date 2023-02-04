package com.aberdote.OVPN4ALL.unit;

import com.aberdote.OVPN4ALL.exception.CustomException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TestUtils {

    private TestUtils() {}

    public static void testException(HttpStatus expectedStatus, String expectedError, Runnable code) {
        final var exception = assertThrows(CustomException.class, code::run);
        assertEquals(expectedStatus, exception.getHttpStatus());
        assertNotNull(exception.getError());
        assertTrue(exception.getError().contains(expectedError));
    }

}
