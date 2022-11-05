package com.aberdote.OVPN4ALL.utils.parser;


import com.aberdote.OVPN4ALL.dto.parser.ConnectionDTO;
import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private final static String TIME_REGEX = "[A-Z][a-z][a-z] [A-Z][a-z][a-z]  [1-3]?[0-9] [0-2][0-9]:[0-5][0-9]:[0-5][0-9] 2[0-9][0-9][0-9]";
    private final static String IP_REGEX = "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private final static String BETWEEN_TIME_AND_IP_REGEX = "[ a-z=0-9]+";
    private final static String CN_REGEX = "CN=";
    private final static String BETWEEN_IP_AND_CN_REGEX = "[:0-9 A-Za-z=,]+ ";
    private final static String USER_EXIT_REGEX = "[:0-9]+ SIGTERM\\[soft,remote\\-exit\\] received, client-instance exiting";


    public static UserInfoDTO getUserInfo(String user, String text) {
        final String CONNECTION_REGEX = String.format("(%s)%s (%s)%s%s%s%n", TIME_REGEX, BETWEEN_TIME_AND_IP_REGEX, IP_REGEX, BETWEEN_IP_AND_CN_REGEX, CN_REGEX, user);
        final String DISCONNECTION_REGEX = String.format("(%s)%s %s/(%s)%s", TIME_REGEX, BETWEEN_TIME_AND_IP_REGEX, user, IP_REGEX, USER_EXIT_REGEX);
        final Matcher connectionMatcher = Pattern.compile(CONNECTION_REGEX).matcher(text);
        final Matcher disconnectionMatcher = Pattern.compile(DISCONNECTION_REGEX).matcher(text);
        final UserInfoDTO userInfoDTO = new UserInfoDTO();
        while(connectionMatcher.find()) {
            final ConnectionDTO connectionDTO = ConnectionDTO.builder().time(connectionMatcher.group(1)).ip(connectionMatcher.group(2)).build();
            userInfoDTO.getConnectionDTOList().add(connectionDTO);
        }
        while(disconnectionMatcher.find()) {
            final ConnectionDTO disconnectionDTO = ConnectionDTO.builder().time(disconnectionMatcher.group(1)).ip(disconnectionMatcher.group(2)).build();;
            userInfoDTO.getDisconnectionDTOList().add(disconnectionDTO);
        }
        return userInfoDTO;
    }

}

