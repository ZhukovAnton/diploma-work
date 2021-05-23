package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.IconEntity;
import com.stanum.skrudzh.model.enums.IconCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IconsRepository extends JpaRepository<IconEntity, Long> {
    List<IconEntity> findByCategory(IconCategoryEnum iconCategory);
}
