package com.aberdote.OVPN4ALL.dto.user;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data @AllArgsConstructor
public class CreateUserRequestDTO {

    @NotNull
    private String name, password;
    @NotNull
    private Collection<RoleDTO> roles;

}
