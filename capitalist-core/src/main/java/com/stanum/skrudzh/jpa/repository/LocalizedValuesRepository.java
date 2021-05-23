package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.system.LocalizedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizedValuesRepository extends JpaRepository<LocalizedValue, Long> {

}
