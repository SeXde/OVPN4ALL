package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.utils.file.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final CommandService commandService;

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

    private final String OVPN4ALL_LOG_FILE = "/var/log/openvpn.log";
  

    @Override
    public File downloadLogs() {
        try {
            final File logs = commandService.downloadLogs();
            if (logs == null || !logs.exists()) {
                final String msg = "Cannot download logs";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return logs;
        } catch (IOException | InterruptedException e) {
            final String msg = String.format("Cannot download logs, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<String> getCreateServerConfigLog(Integer lines) {
        return getLog(workingDir + "/Logs/" + createServerConfigScript + ".log", lines);
    }

    @Override
    public List<String> getCreateUserCertLog(Integer lines) {
        return getLog(workingDir + "/Logs/" + createUserCertScript + ".log", lines);
    }

    @Override
    public List<String> getCreateUserVPNFileLog(Integer lines) {
        return getLog(workingDir + "/Logs/" + createUserConfigScript + ".log", lines);
    }

    @Override
    public List<String> getDeleteUserLog(Integer lines) {
        return getLog(workingDir + "/Logs/" + deleteUserScript + ".log", lines);
    }

    @Override
    public List<String> getOVPNLog(Integer lines) {
        return getLog(OVPN4ALL_LOG_FILE, lines);
    }

    private List<String> getLog(String file, Integer lines) {
        try {
            return FileUtils.getContentFromLineNumber(file, lines);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }


}
