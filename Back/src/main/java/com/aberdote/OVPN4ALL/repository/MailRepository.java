package com.aberdote.OVPN4ALL.repository;

import com.aberdote.OVPN4ALL.entity.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<MailEntity, Long> {
}
