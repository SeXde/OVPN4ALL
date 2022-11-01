package com.aberdote.OVPN4ALL;

import com.aberdote.OVPN4ALL.common.constanst.RoleConstants;
import com.aberdote.OVPN4ALL.dto.RoleDTO;
import com.aberdote.OVPN4ALL.dto.user.CreateUserRequestDTO;
import com.aberdote.OVPN4ALL.entity.RoleEntity;
import com.aberdote.OVPN4ALL.repository.RoleRepository;
import com.aberdote.OVPN4ALL.service.CommandService;
import com.aberdote.OVPN4ALL.service.UserService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
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

    @Autowired
    private CommandService commandService;


    @PostConstruct
    public void populateDatabase () {
        roleRepository.saveAll(RoleConstants.ROLES.stream().map(RoleEntity::new).toList());
        final List<CreateUserRequestDTO> users = List.of(new CreateUserRequestDTO("Admin","dummy@mail.com",
                "Admin",
                List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("AdminUser", "iberia@unboxing.com", "AdminUser",
                List.of(new RoleDTO(RoleConstants.ROLE_USER), new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Pollo", "pollo@unboxing.com", "Pollo",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER), new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER)))
        );
        users.forEach(user -> {
            try {
                commandService.deleteUser(user.getName());
            } catch (IOException | InterruptedException e) {
                return;
            }
        });
        userService.addAllUsers(users);
    }

    private Set<RoleDTO> genRandomRoles() {
        final List<String> roleNames = List.of(RoleConstants.ROLE_USER, RoleConstants.ROLE_ADMIN);
        int randomRoles = (int) ((Math.random() * (RoleConstants.ROLES.size() - 1)) + 1);
        return IntStream.range(0, randomRoles).mapToObj(i -> {
            int randomRole = (int) ((Math.random() * (RoleConstants.ROLES.size())) + 0);
            return new RoleDTO(roleNames.get(randomRole));
        }).collect(Collectors.toSet());
    }

    private void createRandomUsers(int size) {
        Faker faker = new Faker(new Locale("en-US"));
        Set<String> names = IntStream.range(0, size).mapToObj(i -> faker.funnyName().name()).collect(Collectors.toSet());
        Iterator<String> iterator = names.iterator();
        List<CreateUserRequestDTO> createUserRequestDTOS = IntStream.range(0, names.size()).mapToObj(i -> new CreateUserRequestDTO(iterator.next(), faker.internet().safeEmailAddress(), faker.internet().password(), genRandomRoles())).toList();
        userService.addAllUsers(createUserRequestDTOS);
    }

}
