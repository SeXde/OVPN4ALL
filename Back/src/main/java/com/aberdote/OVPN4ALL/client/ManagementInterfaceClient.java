package com.aberdote.OVPN4ALL.client;

import com.aberdote.OVPN4ALL.exception.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ManagementInterfaceClient {

    @Value("${server.management.ip}")
    private static String ip;
    @Value("${server.management.port}")
    private static Integer port;
    private PrintWriter output;
    private BufferedReader input;
    private Socket clientSocket;

    public void init() {
        try {
            clientSocket = new Socket(ip, port);
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e) {
            throw new CustomException(String.format("Cannot establish connection with management interface: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void close() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new CustomException(String.format("Cannot close management interface connection: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean killUser(String userCN){
        return Objects.nonNull(executeCommand(String.format("kill %s", userCN)));
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
