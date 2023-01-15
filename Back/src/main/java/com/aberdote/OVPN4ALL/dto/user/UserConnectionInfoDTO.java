package com.aberdote.OVPN4ALL.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor @NoArgsConstructor
public class UserConnectionInfoDTO {

    private String userName;
    private String socket;
    private Integer bytesIn;
    private Integer bytesOut;
    private String connectedSince;


}
