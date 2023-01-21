package com.aberdote.OVPN4ALL.controller;

import com.aberdote.OVPN4ALL.dto.LogDTO;
import com.aberdote.OVPN4ALL.service.LogService;
import com.aberdote.OVPN4ALL.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor @Controller
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private final LogService logService;
    private final StatusService statusService;


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
    public void sendUsersInfo() {
        template.convertAndSend("/topic/users/info", statusService.getUsersConnected());
    }

    @Scheduled(fixedDelay = 1000)
    public void sendUsage() {
        template.convertAndSend("/topic/server/info", statusService.getThroughput());
    }

}
