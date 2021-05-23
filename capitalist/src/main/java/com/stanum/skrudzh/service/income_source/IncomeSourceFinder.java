package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class IncomeSourceFinder {

    private final IncomeSourcesRepository incomeSourcesRepository;

    public Optional<IncomeSourceEntity> findBorrowIncomeSource(UserEntity userEntity, String currencyCode) {
        return incomeSourcesRepository
                .getFirstByParams(userEntity, true, currencyCode);
    }

    public Optional<IncomeSourceEntity> findVirtualIncomeSource(UserEntity userEntity, String currencyCode) {
        return incomeSourcesRepository
                .findVirtualByUserAndCurrency(userEntity, currencyCode);
    }

    public IncomeSourceEntity findIncomeSourceByActive(ActiveEntity activeEntity) {
        return incomeSourcesRepository.findByActive(activeEntity);
    }

    public IncomeSourceEntity findByActiveId(Long activeId) {
        return incomeSourcesRepository.findByActiveId(activeId);
    }

    public IncomeSourceEntity findById(Long id) {
        return incomeSourcesRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "not found"));
    }

    public Optional<IncomeSourceEntity> findByIdOptional(Long id) {
        return incomeSourcesRepository.findById(id);
    }

    public Set<IncomeSourceEntity> findAllByUser(UserEntity userEntity) {
        return incomeSourcesRepository.findAllByUser(userEntity);
    }

    public List<IncomeSourceEntity> findAllByUserAndIsBorrowAndIsPlanned(UserEntity userEntity, boolean isBorrow, boolean isIncomePlanned) {
        return incomeSourcesRepository
                .getAllByUserAndIsBorrowAndIsIncomePlanned(userEntity, isBorrow, isIncomePlanned);
    }

    public List<IncomeSourceEntity> findAllByUserAndIsPlanned(UserEntity userEntity, boolean isIncomePlanned) {
        return incomeSourcesRepository
                .getByUserAndIsPlanned(userEntity, isIncomePlanned);
    }

    public Set<String> findAllIncomeSourcesCurrencies(UserEntity userEntity) {
        return incomeSourcesRepository.findAllIncomeSourcesCurrencies(userEntity);
    }

    public Optional<Hashable> findByPrototypeKey(UserEntity userEntity, String prototypeKey) {
        return incomeSourcesRepository.findByPrototypeKey(userEntity, prototypeKey);
    }

    public Set<String> findAllUsedPrototypeKeys(UserEntity userEntity) {
        return incomeSourcesRepository.getAllUsedPrototypeKeys(userEntity);
    }

}
