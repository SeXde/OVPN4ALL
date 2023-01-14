package com.aberdote.OVPN4ALL.utils.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteConverterTest {
    @DisplayName("Bytes to MegaBytes converter test")
    @ParameterizedTest
    @ValueSource(floats = {5.0f, 2.0f, 5000.0f, 20.0f, 0.2f, 123342434.0f, 23.0f, 12.0f, 231413245341563245.0f})
    public void BytesToMegaBytes(Float byteNumber) {
        assertEquals(byteNumber / 1000000, ByteConverter.BytesToMegaBytes(byteNumber));
    }
}
