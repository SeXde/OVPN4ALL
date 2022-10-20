package com.aberdote.OVPN4ALL.repository;

import com.aberdote.OVPN4ALL.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {
}
