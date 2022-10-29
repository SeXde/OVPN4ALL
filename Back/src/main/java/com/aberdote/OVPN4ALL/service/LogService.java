package com.aberdote.OVPN4ALL.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service @Transactional
public interface LogService {
    ByteArrayResource downloadLogs();
}
