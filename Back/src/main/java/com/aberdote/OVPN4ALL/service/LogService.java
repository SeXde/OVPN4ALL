package com.aberdote.OVPN4ALL.service;

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
    String getCreateServerConfigLog(Integer lines);
    String getCreateUserCertLog(Integer lines);
    String getCreateUserVPNFileLog(Integer lines);
    String getDeleteUserLog(Integer lines);
    String getOVPNLog(Integer lines);
}
