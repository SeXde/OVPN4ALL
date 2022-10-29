package com.aberdote.OVPN4ALL.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public interface CommandService {

    boolean addUser(String name, String password) throws IOException, InterruptedException;
    boolean editUser(String oldName, String newName, String newPassword) throws IOException, InterruptedException;
    boolean deleteUser(String name) throws IOException, InterruptedException;
    boolean addConfig(String port, String gateway, String netmask) throws IOException, InterruptedException;
    boolean editConfig(String port, String gateway, String netmask) throws IOException, InterruptedException;
    boolean shutdown() throws IOException, InterruptedException;
    boolean startUp() throws IOException, InterruptedException;
    boolean getVPNInfo();
    boolean getUserInfo();
    File downloadOVPNFile(String user) throws IOException, InterruptedException;
    List<File> downloadLogs() throws IOException;
}
