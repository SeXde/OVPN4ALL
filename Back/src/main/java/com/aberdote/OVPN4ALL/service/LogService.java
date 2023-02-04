package com.aberdote.OVPN4ALL.service;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.List;

@Service @Transactional
public interface LogService {
    File downloadLogs();
    List<String> getCreateServerConfigLog(Integer lines);
    List<String> getCreateUserCertLog(Integer lines);
    List<String> getCreateUserVPNFileLog(Integer lines);
    List<String> getDeleteUserLog(Integer lines);
    List<String> getOVPNLog(Integer lines);
}
