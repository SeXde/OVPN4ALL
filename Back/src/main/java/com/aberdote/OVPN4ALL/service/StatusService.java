package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.BandwidthDTO;
import com.aberdote.OVPN4ALL.dto.user.UserConnectionInfoDTO;

import java.util.List;

public interface StatusService {
    boolean isActive();
    boolean turnOn();
    boolean turnOff();
    BandwidthDTO getThroughput();

    List<UserConnectionInfoDTO> getUsersConnected();
}
