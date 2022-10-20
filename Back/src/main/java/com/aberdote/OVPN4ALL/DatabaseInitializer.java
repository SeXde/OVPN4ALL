package com.aberdote.OVPN4ALL;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DatabaseInitializer {


    @Autowired
    private UserService userService;


    @PostConstruct
    public void populateDatabase () {
       userService.addUser(new CreateUserRequestDTO("Admin",
               "Admin",
               List.of(new RoleDTO(RoleConstants.ROLE_OWNER))));
    }

}
