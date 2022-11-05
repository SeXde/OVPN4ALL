package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.utils.script.ScriptExec;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
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
    @Value("${server.name.create.config}")
    private String createServerConfigScript;
    @Value("${server.name.download.logs}")
    private String downloadServerLogs;
    @Value("${server.name.create.iptables}")
    private String createIptables;
    @Value("${server.name.read.ovpn.logs}")
    private String readServerLogs;

    @Value("${server.name.clear.ovpn.logs}")
    private String clearServerLogs;

    @Override
    public boolean addUser(String name, String password) throws IOException, InterruptedException {
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s %s", workingDir, createUserCertScript , workingDir, createUserCertScript, StringConverter.fromStringToHex(name), StringConverter.fromStringToHex(password));
        return executeCommand(cmd, "Create user config");
    }

    @Override
    public boolean editUser(String oldName, String newName, String oldPassword) throws IOException, InterruptedException {
        return deleteUser(oldName) && addUser(newName, oldPassword);
    }

    @Override
    public boolean deleteUser(String name) throws IOException, InterruptedException {
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s", workingDir, deleteUserScript , workingDir, deleteUserScript, StringConverter.fromStringToHex(name));
        return executeCommand(cmd, "delete user");
    }

    @Override
    public boolean addConfig(String port, String gateway, String netmask) throws IOException, InterruptedException {
        final String cmd1 = String.format("%s/Scripts/Server/%s.sh %s Logs/%s.log %s %s %s", workingDir, createServerConfigScript, workingDir, createServerConfigScript, port, gateway, netmask);
        final String cmd2 = String.format("sudo %s/Scripts/Server/%s.sh %s", workingDir, createIptables, port);
        return executeCommand(cmd1, "server config") && executeCommand(cmd2, "iptables");
    }

    @Override
    public boolean editConfig(String port, String gateway, String netmask) throws IOException, InterruptedException {
        return addConfig(port, gateway, netmask);
    }

    @Override
    public void shutdown() throws IOException, InterruptedException {
        ScriptExec.execNoWait("sudo pkill -9 openvpn");
        ScriptExec.execNoWait(String.format("sudo %s/Scripts/Server/%s.sh", workingDir, clearServerLogs));
    }

    @Override
    public void startUp() throws IOException, InterruptedException {
        ScriptExec.execNoWait(String.format("sudo %s/Scripts/Server/%s.sh", workingDir, clearServerLogs));
        final String cmd = String.format("sudo openvpn %s/Server/OVPN4ALL.conf", workingDir);
        log.debug("Executing script: {}", cmd);
        ScriptExec.execNoWait(cmd);
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
    public File downloadOVPNFile(String user, SetupDTO config) throws IOException, InterruptedException {
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s %s %s", workingDir, createUserConfigScript, workingDir, createUserConfigScript, StringConverter.fromStringToHex(user), config.getServer(), config.getPort());
        if (executeCommand(cmd, "create user config")) {
            return new File(String.format("%s/Users/%s.ovpn", workingDir, StringConverter.fromStringToHex(user)));
        }
        return null;
    }

    @Override
    public File downloadLogs() throws IOException, InterruptedException {
        final String cmd = String.format("%s/Scripts/Server/%s.sh %s Logs/%s.log", workingDir, downloadServerLogs, workingDir, downloadServerLogs);
        if (executeCommand(cmd, "download logs")) {
            return new File(String.format("%s/Logs/OVPN4ALL_Logs.zip", workingDir));
        }
        return null;
    }

    @Override
    public boolean clearLogs() throws IOException, InterruptedException {
        final String cmd = String.format("sudo rm -r %s/Logs/*", workingDir);
        return executeCommand(cmd, "clear logs");
    }

    @Override
    public boolean isActive() throws IOException, InterruptedException {
        return ScriptExec.exec("pgrep openvpn") == 0;
    }

    @Override
    public String readOvpnLogs() throws IOException, InterruptedException {
        return ScriptExec.execWithOutput(String.format("sudo %s/Scripts/Server/%s.sh", workingDir, readServerLogs));
    }

    private boolean executeCommand(String command, String logMessage) throws IOException, InterruptedException {
        log.info("Executing script: {}", command);
        final int resultCode = ScriptExec.exec(command);
        if (resultCode != 0) {
            final String message = String.format("%s script did not finish correctly, see logs for more info.\nResult code: '%s'",logMessage, resultCode);
            log.error(message);
            return false;
        }
        return true;
    }


}
