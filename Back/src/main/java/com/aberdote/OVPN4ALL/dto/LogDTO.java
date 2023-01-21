package com.aberdote.OVPN4ALL.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class LogDTO {

    private Integer lineNumber;
    private String content;

}
