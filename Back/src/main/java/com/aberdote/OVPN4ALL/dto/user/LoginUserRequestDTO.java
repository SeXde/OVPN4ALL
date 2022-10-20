package com.aberdote.OVPN4ALL.dto.user;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data @AllArgsConstructor @NoArgsConstructor
@Validated
public class LoginUserRequestDTO {
    @NotNull
    private String name, password;
}
