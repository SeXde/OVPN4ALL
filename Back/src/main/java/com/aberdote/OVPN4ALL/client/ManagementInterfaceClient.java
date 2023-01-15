package com.aberdote.OVPN4ALL.client;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public final class ManagementInterfaceClient {
    private PrintWriter output;
    private BufferedReader input;
    private Socket clientSocket;
    private Boolean isConnected = false;

    private static ManagementInterfaceClient INSTANCE;

    private ManagementInterfaceClient() {}

    public static ManagementInterfaceClient getInstance() {
        return Objects.isNull(INSTANCE) ? new ManagementInterfaceClient() : INSTANCE;
    }

    public void init(String ip, Integer port) {
        if (isConnected) return;
        try {
            clientSocket = new Socket(ip, port);
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            isConnected = true;
        } catch (Exception e) {
            throw new CustomException(String.format("Cannot establish connection with management interface: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void close() {
        if (!isConnected) return;
        try {
            input.close();
            output.close();
            clientSocket.close();
            isConnected = false;
        } catch (IOException e) {
            throw new CustomException(String.format("Cannot close management interface connection: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean killUser(String user){
        return Objects.nonNull(executeCommand(String.format("kill %s", StringConverter.fromStringToHex(user))));
    }

    public String status() {
        executeCommand("status");
        return input.lines().takeWhile(line -> !line.contains("END")).collect(Collectors.joining("\n"));
    }

    private String executeCommand(String command) {
        output.println(command);
        return Strings.EMPTY;
    }
}
