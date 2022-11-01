package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailResponseDTO;
import com.aberdote.OVPN4ALL.entity.MailEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.MailRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.MailService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Service @Slf4j @RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailRepository mailRepository;
    private final CommandService commandService;
    private final ConfigService configService;

    @Override
    public MailResponseDTO setMail(MailRequestDTO mailRequestDTO) {
        mailRepository.deleteAll();
        return EntityConverter.fromMailEntityToMailResponseDTO(mailRepository.save(EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO)));
    }

    @Override
    public void sendMail(String mailSubject, String ovpnFileName) {
        log.debug("Request to send mail to {}", mailSubject);
        mailRepository.findAll().stream().findFirst().ifPresentOrElse(mailEntity -> {
        final Session session = setSession(setProperties(mailEntity), mailEntity.getUsername(), mailEntity.getPassword());
        final Message message = new MimeMessage(session);
            try {
                final File ovpnFile = commandService.downloadOVPNFile(ovpnFileName, configService.getConfig());
                message.setFrom(new InternetAddress(mailEntity.getUsername()));
                message.setRecipients(
                        Message.RecipientType.TO, InternetAddress.parse(mailSubject));
                message.setSubject("Ovpn file has been requested");
                final MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeBodyPart.setContent("Import this config file with openVpn client", "text/html; charset=utf-8");
                mimeBodyPart.attachFile(ovpnFile);
                final Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(mimeBodyPart);
                message.setContent(multipart);
                Transport.send(message);
                log.debug("Mail sent to {} successfully", mailSubject);
            } catch (MessagingException | IOException | InterruptedException e) {
                final String msg = String.format("Cannot send email, ErrorMessage: %s", e.getMessage());
                log.error(msg);
                throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }, () -> {
            final String msg = "There is no email configured";
            log.error(msg);
            throw new CustomException(msg, HttpStatus.NOT_FOUND);
        });
    }

    private Properties setProperties(MailEntity mailEntity) {
        final Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", Boolean.toString(mailEntity.isTtl()));
        properties.put("mail.smtp.host", mailEntity.getSmtpHost());
        properties.put("mail.smtp.port", mailEntity.getSmtpPort());
        properties.put("mail.smtp.ssl.trust", mailEntity.getSmtpHost());
        return properties;
    }

    private Session setSession(Properties properties, String username, String password) {
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

}
