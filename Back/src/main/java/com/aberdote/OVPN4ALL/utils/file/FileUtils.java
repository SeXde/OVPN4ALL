package com.aberdote.OVPN4ALL.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public final class FileUtils {

    private FileUtils() {}

    public static List<String> getContentFromLineNumber(String filePath, Integer lastLines) throws IOException {
        final List<String> lines = Files.lines(Path.of(filePath)).map(String::trim).filter(line -> !line.isBlank()).toList();
        final int lineNumber = Math.max(0, lines.size() - lastLines);
        return IntStream.range(lineNumber, lines.size()).mapToObj(lines::get).toList();
    }

}
