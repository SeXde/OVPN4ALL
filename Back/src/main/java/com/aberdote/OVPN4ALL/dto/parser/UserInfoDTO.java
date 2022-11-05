package com.aberdote.OVPN4ALL.dto.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoDTO {
    List<ConnectionDTO> connectionDTOList = new ArrayList<>();
    List<ConnectionDTO> disconnectionDTOList = new ArrayList<>();

    public boolean isConnected() {
        return connectionDTOList.size() > disconnectionDTOList.size();
    }

}
