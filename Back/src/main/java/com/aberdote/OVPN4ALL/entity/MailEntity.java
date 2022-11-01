package com.aberdote.OVPN4ALL.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data @NoArgsConstructor @Entity
public class MailEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String smtpHost;
    private String smtpPort;
    private boolean ttl;
    private String username;
    private String password;

    public MailEntity(String smtpHost, String smtpPort, boolean ttl, String username, String password) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.ttl = ttl;
        this.username = username;
        this.password = password;
    }
}

