package com.stanum.skrudzh.jpa.repository.system;

import com.stanum.skrudzh.jpa.model.system.Migration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MigrationsRepository extends JpaRepository<Migration, Long> {
    Optional<Migration> findByName(String name);
}
