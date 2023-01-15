package com.aberdote.OVPN4ALL.client;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

@Data
public class ManagementInterfaceClient {
    private PrintWriter output;
    private BufferedReader input;
    private Socket clientSocket;
    private Boolean isConnected = false;

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
        return executeCommand("status");
    }

    private String executeCommand(String command) {
        output.println(command);
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new CustomException(String.format("Cannot read management interface connection response: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
