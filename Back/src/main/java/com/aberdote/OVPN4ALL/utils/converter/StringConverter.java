package com.aberdote.OVPN4ALL.utils.converter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;

public class StringConverter {

    public static String fromStringToHex(String string) {
        return Hex.encodeHexString(string.getBytes(StandardCharsets.UTF_8));
    }

    public static String fromHexToString(String hex) throws DecoderException {
        return new String(Hex.decodeHex(hex), StandardCharsets.UTF_8);
    }

}
