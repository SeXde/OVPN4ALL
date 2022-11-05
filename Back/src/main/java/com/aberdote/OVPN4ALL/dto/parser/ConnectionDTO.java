package com.aberdote.OVPN4ALL.dto.parser;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ConnectionDTO {
    String time, ip;
}
