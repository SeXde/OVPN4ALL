package com.aberdote.OVPN4ALL.dto.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailResponseDTO {
    private String smtpHost;
    private String smtpPort;
    private boolean ttl;
    private String username;
}
