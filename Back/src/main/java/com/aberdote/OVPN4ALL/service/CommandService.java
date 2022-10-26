package com.aberdote.OVPN4ALL.service;

import org.springframework.stereotype.Service;

@Service
public interface CommandService {

    boolean addUser(String name, String password);
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
