package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.service.StatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor @Controller @Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private final LogService logService;
    private final StatusService statusService;

    private final static ObjectMapper objectMapper = new ObjectMapper();


    @MessageMapping("/log/createServerConfig")
    @SendTo("/topic/log/createServerConfig")
    public List<String> sendCreateServerConfigLog() {
        return logService.getCreateServerConfigLog();
    }

    @MessageMapping("/log/createUserCert")
    @SendTo("/topic/log/createUserCert")
    public List<String> sendCreateUserCertLog() {
        return logService.getCreateUserCertLog();
    }

    @MessageMapping("/log/createUserVPNFile")
    @SendTo("/topic/log/createUserVPNFile")
    public List<String> sendCreateUserVPNFileLog() {
        return logService.getCreateUserVPNFileLog();
    }

    @MessageMapping("/log/deleteUser")
    @SendTo("/topic/log/deleteUser")
    public List<String> sendDeleteUserLog() {
        return logService.getDeleteUserLog();
    }

    @MessageMapping("/log/OVPN")
    @SendTo("/topic/log/OVPN")
    public List<String> sendOVPNLog() {
        return logService.getOVPNLog();
    }

    @Scheduled(fixedDelay = 1000)
    public void sendUsersInfo() throws JsonProcessingException {
        if (statusService.isActive()) {
            template.convertAndSend("/topic/users/info", objectMapper.writeValueAsString(statusService.getUsersConnected()));
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void sendUsage() {
        if (statusService.isActive()) {
            template.convertAndSend("/topic/server/info", statusService.getThroughput());
        }
    }

}
