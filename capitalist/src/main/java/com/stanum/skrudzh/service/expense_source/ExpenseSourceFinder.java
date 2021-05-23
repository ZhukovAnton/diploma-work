package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ExpenseSourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseSourceFinder {
    private final ExpenseSourcesRepository expenseSourcesRepository;

    public Set<ExpenseSourceEntity> findExpenseSourcesByUserAndIsVirtual(UserEntity userEntity, Boolean isVirtual) {
        return expenseSourcesRepository.getAllByUserAndIsVirtualAndDeletedAtIsNullOrderByRowOrder(userEntity, isVirtual);
    }

    public Set<ExpenseSourceEntity> findExpenseSourcesByUserAndIsVirtualAndCurrency(UserEntity userEntity,
                                                                                    Boolean isVirtual,
                                                                                    String currencyCode) {
        return expenseSourcesRepository
                .getAllByUserAndIsVirtualAndCurrencyAndDeletedAtIsNullOrderByRowOrder(userEntity, isVirtual, currencyCode);
    }

    public ExpenseSourceEntity findById(Long id) {
        return expenseSourcesRepository.findByIdWithDeleted(id)
                .orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "Can't find ExpenseSource with id " + id));
    }

    public Set<ExpenseSourceEntity> findAllByUserEntity(UserEntity userEntity) {
        return expenseSourcesRepository.findAllByUser(userEntity);
    }

    public Set<String> findAllExpenseSourcesCurrencies(UserEntity userEntity) {
        return expenseSourcesRepository.findAllExpenseSourcesCurrencies(userEntity);
    }

    public Set<ExpenseSourceEntity> findByAccountConnection(AccountConnectionEntity accountConnectionEntity) {
        return expenseSourcesRepository.getAllByAccountConnectionEntity(accountConnectionEntity);
    }

    public Optional<ExpenseSourceEntity> findFirstByParams(UserEntity userEntity, boolean isVirtual, String currencyCode) {
        Pageable firstElement = PageRequest.of(0, 1);
        return expenseSourcesRepository
                .getFirstByParams(userEntity, isVirtual, currencyCode, firstElement)
                .stream()
                .findAny();
    }

    public Set<ExpenseSourceEntity> findAllActual() {
        return expenseSourcesRepository.findAllActual();
    }

    public Set<String> findAllUsedPrototypeKeys(UserEntity userEntity) {
        return expenseSourcesRepository.getAllUsedPrototypeKeys(userEntity);
    }
}
