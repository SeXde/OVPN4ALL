package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.LogDTO;
import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.service.StatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor @Controller @Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private final LogService logService;
    private final StatusService statusService;

    private final static ObjectMapper objectMapper = new ObjectMapper();


    @MessageMapping("/log/createServerConfig/{lines}")
    @SendTo("/topic/log/createServerConfig")
    public LogDTO sendCreateServerConfigLog(@DestinationVariable Integer lines) {
        return logService.getCreateServerConfigLog(lines);
    }

    @MessageMapping("/log/createUserCert/{lines}")
    @SendTo("/topic/log/createUserCert")
    public LogDTO sendCreateUserCertLog(@DestinationVariable Integer lines) {
        return logService.getCreateUserCertLog(lines);
    }

    @MessageMapping("/log/createUserVPNFile/{lines}")
    @SendTo("/topic/log/createUserVPNFile")
    public LogDTO sendCreateUserVPNFileLog(@DestinationVariable Integer lines) {
        return logService.getCreateUserVPNFileLog(lines);
    }

    @MessageMapping("/log/deleteUser/{lines}")
    @SendTo("/topic/log/deleteUser")
    public LogDTO sendDeleteUserLog(@DestinationVariable Integer lines) {
        return logService.getDeleteUserLog(lines);
    }

    @MessageMapping("/log/OVPN/{lines}")
    @SendTo("/topic/log/OVPN")
    public LogDTO sendOVPNLog(@DestinationVariable Integer lines) {
        return logService.getOVPNLog(lines);
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
