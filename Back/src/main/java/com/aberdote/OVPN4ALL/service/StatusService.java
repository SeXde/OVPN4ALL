package com.aberdote.OVPN4ALL.service;

public interface StatusService {
    boolean isActive();
    boolean turnOn();
    boolean turnOff();
}
