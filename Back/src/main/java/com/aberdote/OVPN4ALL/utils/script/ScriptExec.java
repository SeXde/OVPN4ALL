package com.aberdote.OVPN4ALL.utils.script;

import java.io.IOException;

public class ScriptExec {

    public static int exec(String scriptAndArguments) throws IOException, InterruptedException {
        final Process process = Runtime.getRuntime().exec(scriptAndArguments);
        process.waitFor();
        return process.exitValue();
    }

    public static void execNoWait(String scriptAndArguments) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(scriptAndArguments);
    }

}
