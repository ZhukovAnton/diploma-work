package com.stanum.skrudzh.jpa.repository;

import com.stanum.skrudzh.jpa.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    @Query("from ExchangeRateEntity er where er.from = :from and er.to = :to")
    Optional<ExchangeRateEntity> findByFromAndTo(@Param("from") String from, @Param("to") String to);

    @Query("select er.to from ExchangeRateEntity er where er.from = 'USD' and er.isUpdated = true")
    Set<String> findExchangeRateCurrencies();

}
