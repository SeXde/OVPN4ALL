package com.aberdote.OVPN4ALL.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ErrorDTO {
    @NotNull
    private String error, httpStatus;
}
