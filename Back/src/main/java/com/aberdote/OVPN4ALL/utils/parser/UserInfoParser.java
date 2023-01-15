package com.aberdote.OVPN4ALL.utils.parser;

import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import org.apache.commons.codec.DecoderException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UserInfoParser {

    private UserInfoParser() {}

    public static List<UserConnectionInfoDTO> parseUserConnectionInfo(String info) throws DecoderException {
        final String regexPattern = "(\\w+),([\\d.:]+),(\\d+),(\\d+),([ \\w:]+)\\n";
        final Matcher connectionMatcher = Pattern.compile(regexPattern).matcher(info);
        final List<UserConnectionInfoDTO> userConnectionInfoDTOList = new ArrayList<>();
        while (connectionMatcher.find()) {
            userConnectionInfoDTOList.add(
                    UserConnectionInfoDTO.builder()
                            .userName(StringConverter.fromHexToString(connectionMatcher.group(1)))
                            .socket(connectionMatcher.group(2))
                            .bytesIn(Integer.parseInt(connectionMatcher.group(3)))
                            .bytesOut(Integer.parseInt(connectionMatcher.group(4)))
                            .connectedSince(connectionMatcher.group(5))
                            .build()
            );
        }
        return userConnectionInfoDTOList;
    }

}
