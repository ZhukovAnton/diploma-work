package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<ReminderEntity, Long> {
    Optional<ReminderEntity> findFirstByRemindableIdAndRemindableType(Long id, String type);

    Optional<ReminderEntity> findFirstByIdAndRemindableIdAndRemindableType(Long id, Long remId, String type);

    List<ReminderEntity> findAllByStartDate(Timestamp startDate);
}
