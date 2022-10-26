package com.aberdote.OVPN4ALL.utils;

import java.io.IOException;

public class ScriptExec {

    public static int exec(String script) throws IOException, InterruptedException {
            final ProcessBuilder processBuilder = new ProcessBuilder();
            Process process = processBuilder.command("/bin/bash -c "+script).start();
            process.waitFor();
            return process.exitValue();
    }

}
