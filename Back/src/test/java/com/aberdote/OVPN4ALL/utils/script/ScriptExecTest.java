package com.aberdote.OVPN4ALL.utils.script;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ScriptExecTest {

    @Test
    public void execWithOutput() {
        try {
            final String output = ScriptExec.execWithOutput("sudo /home/sexde/OVPN4ALL/Scripts/Server/ReadLogs.sh");
            System.out.printf("Output:\n%s", output);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
