package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.LogDTO;
import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

@Service @Transactional
public interface LogService {
    File downloadLogs();
    UserInfoDTO getUserInfo(String user);
    List<UserInfoDTO> getAllUsersInfo();
    List<UserResponseDTO> getUsersConnected();
    int getNumberOfUsersConnected();
    LogDTO getCreateServerConfigLog(Integer lines);
    LogDTO getCreateUserCertLog(Integer lines);
    LogDTO getCreateUserVPNFileLog(Integer lines);
    LogDTO getDeleteUserLog(Integer lines);
    LogDTO getOVPNLog(Integer lines);
}
