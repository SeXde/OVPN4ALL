package com.aberdote.OVPN4ALL.dto.user;

import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDTO {

    private Long id;
    @NotNull
    private String name, email;
    @NotNull
    private Collection<RoleDTO> roles;
    @NotNull
    private LocalDate createdAt;

}
