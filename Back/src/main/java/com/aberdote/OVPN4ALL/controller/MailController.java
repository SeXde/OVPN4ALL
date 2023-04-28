package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.mail.MailRequestDTO;
import com.aberdote.OVPN4ALL.dto.mail.MailResponseDTO;
import com.aberdote.OVPN4ALL.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@Slf4j
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("")
    ResponseEntity<MailResponseDTO> setEmail(@RequestBody MailRequestDTO mailRequestDTO) {
        log.debug("Request to set Mail");
        return ResponseEntity.ok(mailService.setMail(mailRequestDTO));
    }

    @GetMapping("/{userMail}/file/{fileName}")
    ResponseEntity<Void> sendEmail(@PathVariable String userMail, @PathVariable String fileName) {
        log.debug("Request to send file {} to subject {}", fileName, userMail);
        mailService.sendMail(userMail, fileName);
        return ResponseEntity.ok().build();
    }

}
