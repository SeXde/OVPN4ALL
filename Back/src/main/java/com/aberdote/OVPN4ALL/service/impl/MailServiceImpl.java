package com.aberdote.OVPN4ALL.service.impl;

import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailResponseDTO;
import com.aberdote.OVPN4ALL.entity.MailEntity;
import com.aberdote.OVPN4ALL.exception.CustomException;
import com.aberdote.OVPN4ALL.repository.MailRepository;
import com.aberdote.OVPN4ALL.repository.UserRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.ConfigService;
import com.aberdote.OVPN4ALL.service.MailService;
import com.aberdote.OVPN4ALL.utils.converter.EntityConverter;
import com.aberdote.OVPN4ALL.utils.validator.config.ConfigValidator;
import com.aberdote.OVPN4ALL.utils.validator.user.UserValidator;
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
    private final UserRepository userRepository;
    private final CommandService commandService;
    private final ConfigService configService;

    @Override
    public MailResponseDTO setMail(MailRequestDTO mailRequestDTO) {
        mailRepository.deleteAll();
        if (!ConfigValidator.validateFQDN(mailRequestDTO.getSmtpHost())) {
            final String msg = String.format("%s is not a valid smtp host", mailRequestDTO.getSmtpHost());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!ConfigValidator.validatePort(mailRequestDTO.getSmtpPort())) {
            final String msg = String.format("%s is not a valid smtp port", mailRequestDTO.getSmtpPort());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        if (!UserValidator.validateEmail(mailRequestDTO.getUsername())) {
            final String msg = String.format("%s is not a valid email", mailRequestDTO.getUsername());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.BAD_REQUEST);
        }
        sendTestMail(mailRequestDTO);
        return EntityConverter.fromMailEntityToMailResponseDTO(mailRepository.save(EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO)));
    }

    private void sendTestMail(MailRequestDTO mailRequestDTO) {
        final MailEntity mailEntity = EntityConverter.fromMailRequestDTOToMailEntity(mailRequestDTO);
        final Session session = setSession(setProperties(mailEntity), mailEntity.getUsername(), mailEntity.getPassword());
        final Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(mailEntity.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailEntity.getUsername()));
            message.setSubject("Test Mail");
            final String msg = "This is just a test mail";
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException e) {
            final String msg = String.format("Cannot send email, ErrorMessage: %s", e.getMessage());
            log.error(msg);
            throw new CustomException(msg, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public void sendMail(String mailSubject, String ovpnFileName) {
        log.debug("Request to send mail to {}", mailSubject);
        userRepository.findByNameIgnoreCase(ovpnFileName).orElseThrow(() -> new CustomException("Cannot find user "+ovpnFileName, HttpStatus.NOT_FOUND));
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
