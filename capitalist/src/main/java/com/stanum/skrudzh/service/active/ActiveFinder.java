package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActiveFinder {

    private final ActiveRepository activeRepository;

    public Set<ActiveEntity> findAllByUserEntity(UserEntity userEntity) {
        return activeRepository.findAllByUserEntity(userEntity);
    }

    public Set<ActiveEntity> findAllActivesByBasket(Long basketId) {
        return activeRepository.getAllByBasket(basketId);
    }

    public Set<ActiveEntity> findAllActivesByUser(UserEntity userEntity) {
        return activeRepository.getAllByUser(userEntity);
    }

    public Set<ActiveEntity> findActivesWithMonthlyPlannedPayments(BasketEntity basketEntity) {
        return activeRepository.getActivesWithMonthlyPlannedPayments(basketEntity);
    }

    public Set<ActiveEntity> findActivesWithMonthlyPlannedPayments(UserEntity userEntity) {
        return activeRepository.getActivesWithMonthlyPlannedPayments(userEntity);
    }

    public ActiveEntity findById(Long id) {
        return activeRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "not found"));
    }

    public Set<String> findAllActivesCurrencies(UserEntity userEntity) {
        return activeRepository.findAllActivesCurrencies(userEntity);
    }

    public Set<ActiveEntity> findAllActives() {
        return new HashSet<>(activeRepository.findAll());
    }


}
