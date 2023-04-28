package com.aberdote.OVPN4ALL.utils.converter;

import com.aberdote.OVPN4ALL.dto.user.UserResponseDTO;
import com.aberdote.OVPN4ALL.dto.user.UserResponsePageDTO;
import org.springframework.data.domain.Page;

public class PageConverter {

    public static UserResponsePageDTO fromPagetoUserResponsePageDTO(Page<UserResponseDTO> userPage) {
        return new UserResponsePageDTO(userPage.getContent(), userPage.getContent().size(), userPage.getNumber(), userPage.getTotalElements(), userPage.getTotalPages());
    }

}
