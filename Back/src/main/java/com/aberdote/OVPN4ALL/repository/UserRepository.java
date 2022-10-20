package com.aberdote.OVPN4ALL.repository;

import com.aberdote.OVPN4ALL.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByNameIgnoreCase(String name);

}
