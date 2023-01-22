package com.aberdote.OVPN4ALL.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class LogDTO {

    private Long lineNumber;
    private String content;

}
