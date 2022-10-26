package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.service.CommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j @Service
@Transactional
public class CommandServiceImpl implements CommandService {
    @Override
    public boolean addUser(String name, String password) {
        return false;
    }

    @Override
    public boolean editUser(String name, String password) {
        return false;
    }

    @Override
    public boolean deleteUser(String name) {
        return false;
    }

    @Override
    public boolean addConfig(String port, String gateway, String netmask) {
        return false;
    }

    @Override
    public boolean editConfig(String port, String gateway, String netmask) {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean startUp() {
        return false;
    }

    @Override
    public boolean getVPNInfo() {
        return false;
    }

    @Override
    public boolean getUserInfo() {
        return false;
    }

    @Override
    public boolean downloadOVPNFiles(String user) {
        return false;
    }

    @Override
    public boolean downloadLogs() {
        return false;
    }
}
