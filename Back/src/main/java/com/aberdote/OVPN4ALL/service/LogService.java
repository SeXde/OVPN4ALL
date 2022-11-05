package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.parser.UserInfoDTO;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

@Service @Transactional
public interface LogService {
    File downloadLogs();
    UserInfoDTO getUserInfo(String user);
    List<UserInfoDTO> getAllUsersInfo();
    int getUsersConnected();
}
