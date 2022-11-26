package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.utils.parser.LogParser;
import com.aberdote.OVPN4ALL.utils.converter.StringConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Service @Transactional @Slf4j @RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final CommandService commandService;
    private final UserRepository userRepository;
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
    public UserInfoDTO getUserInfo(String user) {
        userRepository.findByNameIgnoreCase(user).or(() ->{
            final String msg = String.format("%s not found", user);
            log.error(msg);
            throw new CustomException(msg, HttpStatus.NOT_FOUND);
        });
        try {
            final String logLines = commandService.readOvpnLogs();
            if (Strings.isEmpty(logLines)) return null;
            final UserInfoDTO userInfoDTO =  LogParser.getUserInfo(StringConverter.fromStringToHex(user), logLines);
            userInfoDTO.setUserName(user);
            if (isUserInfoEmpty(userInfoDTO)) return null;
            if (isUserInfoInvalid(userInfoDTO)) {
                final String msg = "Log got poisoned";
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return userInfoDTO;
        } catch (IOException | InterruptedException | ExecutionException e) {
            final String msg = String.format("Cannot read ovpn log file, got ErroMessage:%s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<UserInfoDTO> getAllUsersInfo() {
        return userRepository.findAll().stream().map(user -> getUserInfo(user.getName())).filter(Objects::nonNull).toList();
    }

    @Override
    public int getUsersConnected() {
        return getAllUsersInfo().stream().filter(UserInfoDTO::isConnected).toList().size();
    }

    private boolean isUserInfoInvalid(UserInfoDTO userInfoDTO) {
        return userInfoDTO.getConnectionDTOList().size() < userInfoDTO.getDisconnectionDTOList().size();
    }

    private boolean isUserInfoEmpty(UserInfoDTO userInfoDTO) {
        return userInfoDTO.getConnectionDTOList().isEmpty() && userInfoDTO.getDisconnectionDTOList().isEmpty();
    }


}
