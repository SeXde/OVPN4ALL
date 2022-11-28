package com.aberdote.OVPN4ALL.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserResponsePageDTO {
    List<UserResponseDTO> users;
    Integer currentUsers;
    Integer currentPage;
    Long totalUsers;
    Integer totalPages;
}
