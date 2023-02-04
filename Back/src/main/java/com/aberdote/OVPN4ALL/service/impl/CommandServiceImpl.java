package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.client.ManagementInterfaceClient;
import com.aberdote.OVPN4ALL.dto.SetupDTO;
import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import com.aberdote.OVPN4ALL.utils.parser.UserInfoParser;
import com.aberdote.OVPN4ALL.utils.script.ScriptExec;
import lombok.extern.slf4j.Slf4j;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Value("${server.password}")
    private String password;

    @Value("${server.management.ip}")
    private String ip;
    @Value("${server.management.port}")
    private Integer port;

    private final ManagementInterfaceClient managementInterfaceClient = ManagementInterfaceClient.getInstance();

    @Override
    public boolean addUser(String name, String password) throws IOException, InterruptedException {
        return executeCommand(String.format("%s/Scripts/User/%s.sh", workingDir, createUserCertScript),
                "Create user config", workingDir,
                String.format("Logs/%s.log", createUserCertScript),
                StringConverter.fromStringToHex(name), StringConverter.fromStringToHex(password));
    }

    @Override
    public boolean editUser(String oldName, String newName, String oldPassword) throws IOException, InterruptedException {
        return deleteUser(oldName) && addUser(newName, oldPassword);
    }

    @Override
    public boolean deleteUser(String name) throws IOException, InterruptedException {
        return executeCommand(String.format("%s/Scripts/User/%s.sh", workingDir, deleteUserScript),
                "delete user", workingDir,
                String.format("Logs/%s.log", deleteUserScript), StringConverter.fromStringToHex(name));
    }

    @Override
    public boolean addConfig(String port, String gateway, String netmask) throws IOException, InterruptedException {
        return executeCommand(String.format("%s/Scripts/Server/%s.sh", workingDir, createServerConfigScript),
                "server config", workingDir,
                String.format("Logs/%s.log", createServerConfigScript), port, gateway, netmask) &&
                executeCommand("sudo", "iptables", "-S",
                        String.format("%s/Scripts/Server/%s.sh", workingDir, createIptables), port);
    }

    @Override
    public boolean editConfig(String port, String gateway, String netmask) throws IOException, InterruptedException {
        return addConfig(port, gateway, netmask);
    }

    @Override
    public void shutdown() throws IOException, InterruptedException {
        ScriptExec.execNoWait("sudo pkill -9 openvpn");
        managementInterfaceClient.close();
    }

    @Override
    public void startUp() throws IOException, InterruptedException {
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
        if (executeCommand(String.format("%s/Scripts/User/%s.sh", workingDir, createUserConfigScript),
                "create user config",
                workingDir, String.format("Logs/%s.log", createUserConfigScript),
                StringConverter.fromStringToHex(user), config.getServer(), config.getPort())) {
            return new File(String.format("%s/Users/%s.ovpn", workingDir, StringConverter.fromStringToHex(user)));
        }
        return null;
    }

    @Override
    public File downloadLogs() throws IOException, InterruptedException {
        if (executeCommand(String.format("%s/Scripts/Server/%s.sh", workingDir, downloadServerLogs),
                "download logs", workingDir, String.format("Logs/%s.log", downloadServerLogs))) {
            return new File(String.format("%s/Logs/OVPN4ALL_Logs.zip", workingDir));
        }
        return null;
    }

    @Override
    public boolean clearLogs() throws IOException, InterruptedException {
        return executeCommand("sudo", "clear logs",
                "-S", "rm", "-r", String.format("%s/Logs/*", workingDir));
    }

    @Override
    public boolean isActive() throws IOException, InterruptedException {
        return new ProcBuilder("pgrep").withArg("openvpn").ignoreExitStatus().run().getExitValue() == 0;
    }

    @Override
    public String readOvpnLogs() throws IOException, InterruptedException, ExecutionException {
        return new ProcBuilder("sudo")
                .withArgs("-S", "cat", "/var/log/openvpn.log")
                .withInput(password)
                .run()
                .getOutputString();
    }

    @Override
    public String[] readThroughput() throws IOException, InterruptedException, ExecutionException {
        final ProcResult result = new ProcBuilder("grep")
                .withArgs("tun0", "/proc/net/dev")
                .run();
        final String commandOutput =  result.getOutputString();
        if (commandOutput == null || commandOutput.isEmpty()) return new String[]{};
        final Matcher matcher = Pattern.compile(": +([0-9]+) +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +[0-9]+ +([0-9]+)").matcher(commandOutput);
        if (matcher.find()) {
            return new String[]{matcher.group(1), matcher.group(2)};
        }
        return new String[]{};
    }

    @Override
    public boolean killClient(String clientCN) throws IOException, InterruptedException {
        if (!isActive()) {
            return false;
        }
        managementInterfaceClient.init(ip, port);
        return managementInterfaceClient.killUser(clientCN);
    }

    @Override
    public List<UserConnectionInfoDTO> getUsersConnected() {
        managementInterfaceClient.init(ip, port);
        final List<UserConnectionInfoDTO> userConnectionInfoDTOList = UserInfoParser.parseUserConnectionInfo(managementInterfaceClient.status());
        userConnectionInfoDTOList.sort(Comparator.comparing(UserConnectionInfoDTO::getUserName));
        return userConnectionInfoDTOList;
    }

    private boolean executeCommand(String command, String logMessage, String ... args) throws IOException, InterruptedException {
        log.debug("Executing command: {} with args {}", command, args);
        final ProcResult result = command.equals("sudo") ?
                new ProcBuilder(command)
                    .withArgs(args)
                    .ignoreExitStatus()
                    .withInput(password)
                    .run()
                :
                new ProcBuilder(command)
                        .withArgs(args)
                        .ignoreExitStatus()
                        .run();
        final int resultCode = result.getExitValue();
        if (resultCode != 0) {
            final String message = String.format("%s script did not finish correctly, see logs for more info.\nResult code: '%s'",logMessage, resultCode);
            log.error(message);
            return false;
        }
        return true;
    }


}
