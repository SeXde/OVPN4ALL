package com.aberdote.OVPN4ALL.utils.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ScriptExec {


    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    public static int exec(String scriptAndArguments) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(scriptAndArguments);
        process.waitFor();
        return process.exitValue();
    }

    public static void execNoWait(String scriptAndArguments) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(scriptAndArguments);
    }

    public static String execWithOutput(String scriptAndArguments) throws IOException, InterruptedException, ExecutionException {
        final Process process = Runtime.getRuntime().exec(scriptAndArguments);
        final StreamGobbler streamGobbler =
                new StreamGobbler(process.getInputStream(), System.out::println);
        Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
        final int exitCode = process.waitFor();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final String output = bufferedReader.lines().collect(Collectors.joining("\n"));
        future.get();
        if (exitCode != 0) return null;
        return output;
    }

}
