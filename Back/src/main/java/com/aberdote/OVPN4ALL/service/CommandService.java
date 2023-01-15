package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public interface CommandService {

    boolean addUser(String name, String password) throws IOException, InterruptedException;
    boolean editUser(String oldName, String newName, String newPassword) throws IOException, InterruptedException;
    boolean deleteUser(String name) throws IOException, InterruptedException;
    boolean addConfig(String port, String gateway, String netmask) throws IOException, InterruptedException;
    boolean editConfig(String port, String gateway, String netmask) throws IOException, InterruptedException;
    void shutdown() throws IOException, InterruptedException;
    void startUp() throws IOException, InterruptedException;
    boolean getVPNInfo();
    boolean getUserInfo();
    File downloadOVPNFile(String user, SetupDTO config) throws IOException, InterruptedException;
    File downloadLogs() throws IOException, InterruptedException;
    boolean clearLogs() throws IOException, InterruptedException;
    boolean isActive() throws IOException, InterruptedException;
    String readOvpnLogs() throws IOException, InterruptedException, ExecutionException;
    String[] readThroughput() throws IOException, InterruptedException, ExecutionException;

    boolean killClient(String clientCN) throws IOException, InterruptedException;

    List<UserConnectionInfoDTO> getUsersConnected();
}
