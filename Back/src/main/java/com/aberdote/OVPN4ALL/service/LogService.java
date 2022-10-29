package com.aberdote.OVPN4ALL.service;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;

@Service @Transactional
public interface LogService {
    File downloadLogs();
}
