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
    List<String> getCreateServerConfigLog(Integer lines);
    List<String> getCreateUserCertLog(Integer lines);
    List<String> getCreateUserVPNFileLog(Integer lines);
    List<String> getDeleteUserLog(Integer lines);
    List<String> getOVPNLog(Integer lines);
}
