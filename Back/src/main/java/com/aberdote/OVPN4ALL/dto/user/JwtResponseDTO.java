package com.aberdote.OVPN4ALL.dto.user;

public class JwtResponseDTO {
    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

    public JwtResponseDTO(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}
