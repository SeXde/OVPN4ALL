package com.aberdote.OVPN4ALL.utils.parser;

import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogParserTest {

    @Test
    public void getUserInfo() throws IOException {
        final String user = "5a6f65";
        final String text = String.join("\n", Files.readAllLines(Path.of("src/test/resources/log_file.log")));
        final UserInfoDTO userInfoDTO =  LogParser.getUserInfo(user, text);
        System.out.println(userInfoDTO);
    }

}
