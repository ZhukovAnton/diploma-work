package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    Optional<SessionEntity> findByToken(String token);
}
