package com.aberdote.OVPN4ALL;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.service.UserService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DatabaseInitializer {


    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    private final static int USERS_NUMBER = 10;


    @PostConstruct
    public void populateDatabase () {

        roleRepository.saveAll(RoleConstants.ROLES.stream().map(RoleEntity::new).toList());
        Faker faker = new Faker(new Locale("en-US"));
        Set<String> names = IntStream.range(0, USERS_NUMBER).mapToObj(i -> faker.funnyName().name()).collect(Collectors.toSet());
        Iterator<String> iterator = names.iterator();
        List<CreateUserRequestDTO> createUserRequestDTOS = IntStream.range(0, names.size()).mapToObj(i -> new CreateUserRequestDTO(iterator.next(), faker.internet().safeEmailAddress(), faker.internet().password(), genRandomRoles())).toList();
        userService.addAllUsers(createUserRequestDTOS);
        userService.addUser(new CreateUserRequestDTO("Admin","dummy@mail.com",
                "Admin",
                List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))));
    }

    private Set<RoleDTO> genRandomRoles() {
        final List<String> roleNames = List.of(RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN);
        int randomRoles = (int) ((Math.random() * (RoleConstants.ROLES.size() - 1)) + 1);
        return IntStream.range(0, randomRoles).mapToObj(i -> {
            int randomRole = (int) ((Math.random() * (RoleConstants.ROLES.size())) + 0);
            return new RoleDTO(roleNames.get(randomRole));
        }).collect(Collectors.toSet());
    }

}
