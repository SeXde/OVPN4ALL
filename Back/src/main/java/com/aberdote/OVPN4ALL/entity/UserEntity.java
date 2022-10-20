package com.aberdote.OVPN4ALL.entity;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;


@Entity @Data @NoArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull @Column(unique = true)
    private String name;

    @NotNull
    private String email;
    @NotNull
    private String password;
    private LocalDate createdAt;
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<RoleEntity> roles = new HashSet<>();

    public UserEntity(String name, String password, Set<RoleEntity> roles, String email) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        createdAt = LocalDate.now();
    }
}
