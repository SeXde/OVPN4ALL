package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.utils.script.ScriptExec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Slf4j @Service
@Transactional
public class CommandServiceImpl implements CommandService {

    @Value("${server.path.working-directory}")
    private String workingDir;
    @Value("${server.name.user.create.config}")
    private String createUserConfigScript;
    @Value("${server.name.user.create.cert}")
    private String createUserCertScript;
    @Value("${server.name.user.delete}")
    private String deleteUserScript;

    @Override
    public boolean addUser(String name, String password) throws IOException, InterruptedException {
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s %s", workingDir, createUserCertScript , workingDir, createUserCertScript, name.replaceAll(" ", "").replaceAll("'", " "), password);
        log.info("Executing script: {}", cmd);
        final int resultCode = ScriptExec.exec(cmd);
        if (resultCode != 0) {
            final String message = String.format("Create user config script did not finish correctly, see logs for more info.\nResult code: '%s'", resultCode);
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
