package com.aberdote.OVPN4ALL.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity @Data @NoArgsConstructor @AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String name;
    private String email;
    private String password;
    private LocalDate createdAt;
    @ManyToMany
    @JoinTable(
            name = "user_entity_roles",
            joinColumns = @JoinColumn(
                    name = "user_entity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_entity_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles = new HashSet<>();

    public UserEntity(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        createdAt = LocalDate.now();
    }

}
