package com.aberdote.OVPN4ALL.utils.file;

import com.aberdote.OVPN4ALL.dto.LogDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class FileUtils {

    private FileUtils() {}

    public static LogDTO getContentFromLineNumber(String filePath, Integer lineNumber) throws IOException {
        if (lineNumber <= 0) throw new RuntimeException("line number cannot be negative or zero");
        final AtomicReference<Integer> lineCount = new AtomicReference<>(1);
        return LogDTO.builder().content(Files.lines(Path.of(filePath))
                .dropWhile(line -> lineCount.getAndSet(lineCount.get() + 1) < lineNumber)
                .collect(Collectors.joining("\n"))).lineNumber(lineCount.get()).build();
    }

}
