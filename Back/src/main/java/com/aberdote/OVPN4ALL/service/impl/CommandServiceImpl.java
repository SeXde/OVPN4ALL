package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.utils.script.ScriptExec;
import com.aberdote.OVPN4ALL.utils.validator.converter.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    @Value("${server.name.server.create.config}")
    private String createServerConfigScript;
    @Autowired
    private ConfigService configService;

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
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s %s %s", workingDir, createServerConfigScript, workingDir, createServerConfigScript, port, gateway, netmask);
        return executeCommand(cmd, "delete user");
    }

    @Override
    public boolean editConfig(String port, String gateway, String netmask) throws IOException, InterruptedException {
        return addConfig(port, gateway, netmask);
    }

    @Override
    public boolean shutdown() throws IOException, InterruptedException {
        final String cmd = "pkill -9 openvpn";
        return executeCommand(cmd, "shutdown vpn");
    }

    @Override
    public boolean startUp() throws IOException, InterruptedException {
        final String cmd = String.format("openvpn %s/Server/OVPN4ALL.conf", workingDir);
        return executeCommand(cmd, "start vpn");
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
    public File downloadOVPNFile(String user) throws IOException, InterruptedException {
        SetupDTO config = configService.getConfig();
        final String cmd = String.format("%s/Scripts/User/%s.sh %s Logs/%s.log %s %s %s", workingDir, createUserConfigScript, workingDir, createUserConfigScript, StringConverter.fromStringToHex(user), config.getServer(), config.getPort());
        if (executeCommand(cmd, "create user config")) {
            return new File(String.format("%s/Users/%s.ovpn", workingDir, StringConverter.fromStringToHex(user)));
        }
        return null;
    }

    @Override
    public File downloadLogs() throws IOException {
        final List<File> files = Files.walk(Paths.get(String.format("%s/Logs", workingDir))).map(path -> new File(path.toUri())).filter(File::isFile).toList();
        try {
            final ZipOutputStream logsZip = new ZipOutputStream(new FileOutputStream(String.format("%s/logs.zip", workingDir)));
            for (File file : files) {
                final FileInputStream fileInputStream = new FileInputStream(file);
                final ZipEntry zipEntry = new ZipEntry(file.getName());
                logsZip.putNextEntry(zipEntry);
                final byte[] buffer = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buffer)) > 0) {
                    logsZip.write(buffer, 0, len);
                }
                logsZip.closeEntry();
                fileInputStream.close();
            }
        } catch (IOException e) {
            final String msg = String.format("Cannot compress logs files, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new File(String.format("%s/logs.zip", workingDir));
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
