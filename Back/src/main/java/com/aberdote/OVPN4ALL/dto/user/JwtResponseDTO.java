package com.aberdote.OVPN4ALL.dto.user;

public class JwtResponseDTO {
    private static final long serialVersionUID = 1L;
    private final String token;
    public JwtResponseDTO(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
