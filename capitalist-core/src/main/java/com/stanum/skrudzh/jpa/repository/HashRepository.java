package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.HashEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HashRepository extends JpaRepository<HashEntity, Long> {

//    @Query(value = "select * from hashes where user_id = ?1 " +
//            "and salt_edge_category like lower(concat('%', ?2, '%')) and hashable_type = ?3",
//            nativeQuery = true)
    @Query("from HashEntity where user = :userEntity " +
            "and lower(saltEdgeCategory) like (concat('% ', lower(:saltEdgeCategory), '%')) " +
            "and hashableType = :hashableType and hashableCurrency = :currency")
    Optional<HashEntity> findHashByParams(UserEntity userEntity, String saltEdgeCategory, HashableTypeEnum hashableType, String currency);

    List<HashEntity> findAllByUserId(Long id);

    Optional<HashEntity> findByUserIdAndPrototypeKey(Long id, String prototypeKey);

}
