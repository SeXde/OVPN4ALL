package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.BandwidthDTO;

public interface StatusService {
    boolean isActive();
    boolean turnOn();
    boolean turnOff();
    BandwidthDTO getThroughput();
}
