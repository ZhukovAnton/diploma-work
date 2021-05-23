package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ProviderMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvidersMetaRepository extends JpaRepository<ProviderMeta, Long> {

    Optional<ProviderMeta> findByPrototypeKey(String prototype);
}
