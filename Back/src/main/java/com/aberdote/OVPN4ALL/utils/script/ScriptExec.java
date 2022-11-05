package com.aberdote.OVPN4ALL.utils.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ScriptExec {

    public static int exec(String scriptAndArguments) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(scriptAndArguments);
        process.waitFor();
        return process.exitValue();
    }

    public static void execNoWait(String scriptAndArguments) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(scriptAndArguments);
    }

    public static String execWithOutput(String scriptAndArguments) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(scriptAndArguments);
        process.waitFor();
        if (process.exitValue() != 0) return null;
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }

}
