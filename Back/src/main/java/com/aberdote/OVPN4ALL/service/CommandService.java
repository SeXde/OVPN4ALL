package com.aberdote.OVPN4ALL.service;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface CommandService {

    boolean addUser(String name, String password, String server, String port) throws IOException, InterruptedException;
    boolean editUser(String name, String password);
    boolean deleteUser(String name);
    boolean addConfig(String port, String gateway, String netmask);
    boolean editConfig(String port, String gateway, String netmask);
    boolean shutdown();
    boolean startUp();
    boolean getVPNInfo();
    boolean getUserInfo();
    boolean downloadOVPNFiles(String user);
    boolean downloadLogs();
}
