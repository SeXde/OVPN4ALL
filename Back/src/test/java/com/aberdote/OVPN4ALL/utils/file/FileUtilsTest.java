package com.aberdote.OVPN4ALL.utils.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    @Test
    public void testFileRead_correct() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 64;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath, line);
        assertNotNull(result);
    }

    @Test
    public void testFileRead_first_line() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 1;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath, line);
        assertNotNull(result);
    }

    @Test
    public void testFileRead_last_line() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 556;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath, line);
        assertNotNull(result);
    }

}
