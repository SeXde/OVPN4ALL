package com.aberdote.OVPN4ALL.utils.parser;

import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;
import org.apache.commons.codec.DecoderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserInfoParserTest {

    @DisplayName("Test user info parser")
    @Test
    void parseUserConnectionInfoTest() throws DecoderException {
        final List<UserConnectionInfoDTO> expectedList = List.of(
                UserConnectionInfoDTO.builder()
                        .userName("Alvaro")
                        .socket("203.4.183.109:1234")
                        .bytesIn(24830)
                        .bytesOut(35030)
                        .connectedSince("Sun Jan 15 09:57:51 2023")
                        .build(),
                UserConnectionInfoDTO.builder()
                        .userName("Paco")
                        .socket("2.1.234.89:1234")
                        .bytesIn(1234)
                        .bytesOut(0)
                        .connectedSince("Mon Feb 12 12:34:01 1089")
                        .build(),
                UserConnectionInfoDTO.builder()
                        .userName("Juan")
                        .socket("1.234.123.1:666")
                        .bytesIn(19875)
                        .bytesOut(3234234)
                        .connectedSince("Wed Jun 09 21:23:25 8012")
                        .build()
        );
        final String text ="""
                OpenVPN CLIENT LIST
                Updated,Sun Jan 15 09:58:17 2023
                Common Name,Real Address,Bytes Received,Bytes Sent,Connected Since
                416c7661726f,203.4.183.109:1234,24830,35030,Sun Jan 15 09:57:51 2023
                5061636f,2.1.234.89:1234,1234,0,Mon Feb 12 12:34:01 1089
                4a75616e,1.234.123.1:666,19875,3234234,Wed Jun 09 21:23:25 8012
                ROUTING TABLE
                Virtual Address,Common Name,Real Address,Last Ref
                10.8.0.2,416c7661726f,31.4.183.109:51750,Sun Jan 15 09:58:10 2023
                GLOBAL STATS
                Max bcast/mcast queue length,1
                END
                """;
        final List<UserConnectionInfoDTO> obtainedList = UserInfoParser.parseUserConnectionInfo(text);
        assertEquals(expectedList.size(), obtainedList.size());
        assertTrue(
                IntStream.range(0, expectedList.size())
                        .allMatch(
                                i -> {
                                    final UserConnectionInfoDTO expectedUserInfo = expectedList.get(i);
                                    final UserConnectionInfoDTO obtainedUserInfo = obtainedList.get(i);
                                    return Objects.equals(expectedUserInfo.getUserName(), obtainedUserInfo.getUserName()) &&
                                            Objects.equals(expectedUserInfo.getSocket(), obtainedUserInfo.getSocket()) &&
                                            Objects.equals(expectedUserInfo.getBytesIn(), obtainedUserInfo.getBytesIn()) &&
                                            Objects.equals(expectedUserInfo.getBytesOut(), obtainedUserInfo.getBytesOut()) &&
                                            Objects.equals(expectedUserInfo.getConnectedSince(), obtainedUserInfo.getConnectedSince());
                                }
                        )
        );
    }

}
