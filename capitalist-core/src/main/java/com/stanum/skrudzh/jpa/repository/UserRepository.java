package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("from UserEntity u where lower(u.email) = lower(:email)")
    Optional<UserEntity> findByEmail(@Param("email")String email);

    Optional<UserEntity> findByEmailConfirmationCode(String code);

    @Query("from UserEntity where saltEdgeCustomerId is null or saltEdgeCustomerSecret is null")
    List<UserEntity> findAllWithoutSaltEdge();

    Boolean existsByEmailIgnoreCase(String email);
}
