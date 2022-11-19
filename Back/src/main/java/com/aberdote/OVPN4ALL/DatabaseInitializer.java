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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${email}")
    private String email;


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
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Alvaro", email, "Alvaro",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Malecom", "Malecom@unboxing.com", "Malecom",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Salero", "Salero@unboxing.com", "Salero",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe1", "Zoe1@unboxing.com", "Zoe1",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe2", "Zoe2@unboxing.com", "Zoe2",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe3", "Zoe3@unboxing.com", "Zoe3",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe4", "Zoe4@unboxing.com", "Zoe4",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe5", "Zoe5@unboxing.com", "Zoe5",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe7", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe8", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe9", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe10", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe11", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe12", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe13", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe14", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe15", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe16", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe17", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe18", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe19", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe20", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe21", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe22", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe23", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe24", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe25", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe26", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe27", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe28", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe29", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe30", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe31", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe32", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe33", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe34", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe35", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe36", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe37", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_USER))),
                new CreateUserRequestDTO("Zoe38", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe39", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN))),
                new CreateUserRequestDTO("Zoe40", "pollo@unboxing.com", "Zoe",
                        List.of(new RoleDTO(RoleConstants.ROLE_ADMIN)))
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
