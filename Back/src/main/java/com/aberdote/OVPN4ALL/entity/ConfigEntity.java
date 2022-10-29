package com.aberdote.OVPN4ALL.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity @Data @NoArgsConstructor
public class ConfigEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String port, gateway, netmask, server;

    public ConfigEntity(String port, String gateway, String netmask, String server) {
        this.port = port;
        this.gateway = gateway;
        this.netmask = netmask;
        this.server = server;
    }
}
