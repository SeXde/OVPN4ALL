package com.aberdote.OVPN4ALL;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class DatabaseInitializer {


    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;


    @PostConstruct
    public void populateDatabase () {

        roleRepository.saveAll(RoleConstants.ROLES.stream().map(RoleEntity::new).toList());

       userService.addUser(new CreateUserRequestDTO("Admin","dummy@mail.com",
               "Admin",
               List.of(new RoleDTO(RoleConstants.ROLE_OWNER))));
    }

}
