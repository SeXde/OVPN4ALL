package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

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
}
