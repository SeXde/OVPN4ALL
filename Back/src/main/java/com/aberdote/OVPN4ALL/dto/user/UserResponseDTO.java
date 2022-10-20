package com.aberdote.OVPN4ALL.dto.user;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

@Data @AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    @NotNull
    private String name;
    @NotNull
    private Collection<RoleDTO> roles;
    @NotNull
    private LocalDate createdAt;

}
