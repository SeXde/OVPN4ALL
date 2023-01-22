package com.aberdote.OVPN4ALL.utils.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public final class FileUtils {

    private FileUtils() {}

    public static String getContentFromLineNumber(String filePath, Integer lineNumber) throws IOException {
        log.info("Getting {} lines from file {}", lineNumber, filePath);
        if (lineNumber <= 0) throw new RuntimeException("line number cannot be negative or zero");
        final AtomicReference<Integer> lineCount = new AtomicReference<>(1);
        return Files.lines(Path.of(filePath))
                .dropWhile(line -> lineCount.getAndSet(lineCount.get() + 1) < lineNumber)
                .collect(Collectors.joining("\n"));
    }

}
