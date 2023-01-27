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
        final List<String> result = FileUtils.getContentFromLineNumber(filePath);
        assertNotNull(result);
    }

    @Test
    public void testFileRead_first_line() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 1;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath);
        assertNotNull(result);
    }

    @Test
    public void testFileRead_last_line() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 556;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath);
        assertNotNull(result);
    }

    @Test
    public void testFileRead_negative_number() {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = -1;
        assertThrows(RuntimeException.class, () -> FileUtils.getContentFromLineNumber(filePath));
    }

    @Test
    public void testFileRead_zero() {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 0;
        assertThrows(RuntimeException.class, () -> FileUtils.getContentFromLineNumber(filePath));
    }

    @Test
    public void testFileRead_too_long_number() throws IOException {
        final String filePath = "src/test/resources/publicIpList.txt";
        final Integer line = 900;
        final List<String> result = FileUtils.getContentFromLineNumber(filePath);
        assertEquals(0, result.size());
    }

}
