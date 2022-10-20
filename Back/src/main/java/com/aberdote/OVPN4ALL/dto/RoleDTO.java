package com.aberdote.OVPN4ALL.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RoleDTO {

    @NotNull
    private String roleName;

}
