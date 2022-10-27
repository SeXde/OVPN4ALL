package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.common.constanst.ScriptsConstants;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.utils.script.ScriptExec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j @Service
@Transactional
public class CommandServiceImpl implements CommandService {
    @Override
    public boolean addUser(String name, String password, String server, String port) throws IOException, InterruptedException {
        log.info("Executing create user {}", name);
        final int resultCode = ScriptExec.exec(String.format("%s%s %s %s %s %s", ScriptsConstants.APP_PATH, ScriptsConstants.CREATE_USER_SH, name, password, server, port));
        if (resultCode != 0) {
            final String message = String.format("User script did not finish correctly, see logs for more info.\nResult code: '%s'", resultCode);
            log.error(message);
            return false;
        }
        return true;
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
