package com.aberdote.OVPN4ALL.service;

import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailResponseDTO;

public interface MailService {
    MailResponseDTO setMail(MailRequestDTO mailRequestDTO);
    void sendMail(String mailSubject, String ovpnFile);
}
