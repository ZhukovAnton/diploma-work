package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.repository.ExpenseCategoriesRepository;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.service.basket.BasketFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryFinder {

    private final ExpenseCategoriesRepository expenseCategoriesRepository;

    private final BasketFinder basketFinder;

    public ExpenseCategoryEntity findByCredit(CreditEntity creditEntity) {
        return expenseCategoriesRepository.findByCreditEntityAndDeletedAtIsNull(creditEntity).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public ExpenseCategoryEntity findByCreditWithDeleted(CreditEntity creditEntity) {
        return expenseCategoriesRepository.findByCreditEntityWithDeleted(creditEntity).orElse(null);
    }

    public Set<ExpenseCategoryEntity> findByBasketWithMonthlyCents(BasketEntity basketEntity) {
        return expenseCategoriesRepository.getExpenseCategoriesWithMonthlyPlannedCents(basketEntity);
    }

    public Set<ExpenseCategoryEntity> findAllByUser(UserEntity userEntity) {
        return expenseCategoriesRepository.findAllByUser(userEntity);
    }

    public Set<ExpenseCategoryEntity> findAllByBasket(BasketEntity basketEntity) {
        return expenseCategoriesRepository.findAllByBasket(basketEntity);
    }

    public ExpenseCategoryEntity findById(Long id) {
        return expenseCategoriesRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "not found"));
    }

    public Optional<ExpenseCategoryEntity> findByIdOptional(Long id) {
        return expenseCategoriesRepository.findById(id);
    }

    public Set<ExpenseCategoryEntity> findByBasketAndNoBorrow(Long basketId, boolean noBorrows) {
        Set<ExpenseCategoryEntity> expenseCategoryEntities;
        if (noBorrows) {
            expenseCategoryEntities = expenseCategoriesRepository
                    .getExpenseCategoryEntitiesByBasketAndBorrow(basketId, false);
        } else {
            expenseCategoryEntities = expenseCategoriesRepository
                    .getExpenseCategoryEntitiesByBasket(basketId);
        }
        return expenseCategoryEntities;
    }

    public Set<ExpenseCategoryEntity> findByUserAndNoBorrow(Long userId, boolean noBorrows) {
        Set<ExpenseCategoryEntity> expenseCategoryEntities;
        if (noBorrows) {
            expenseCategoryEntities = expenseCategoriesRepository
                    .getExpenseCategoryEntitiesByUserAndBorrow(userId, false);
        } else {
            expenseCategoryEntities = expenseCategoriesRepository
                    .getExpenseCategoryEntitiesByUser(userId);
        }
        return expenseCategoryEntities;
    }

    public Optional<ExpenseCategoryEntity> findBorrowExpenseCategoryByParams(BasketEntity basketEntity, String currencyCode) {
        return expenseCategoriesRepository.getBorrowExpenseCategoryFilteredByParams(basketEntity, true, currencyCode);
    }

    public Set<String> findAllExpenseCategoriesCurrencies(UserEntity userEntity) {
        return expenseCategoriesRepository.findAllExpenseCategoriesCurrencies(userEntity);
    }

    public Set<ExpenseCategoryEntity> findAllWithPlannedExpenses(UserEntity userEntity) {
        return expenseCategoriesRepository.findAllWithPlannedExpenses(userEntity);
    }

    public Set<ExpenseCategoryEntity> findAllWithoutPlannedExpenses(UserEntity userEntity) {
        return expenseCategoriesRepository.findAllWithoutPlannedExpenses(userEntity);
    }

    public Optional<ExpenseCategoryEntity> findVirtualExpenseCategoryByParams(UserEntity userEntity, String currencyCode){
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        return expenseCategoriesRepository.getVirtualExpenseCategory(joyBasket, currencyCode);
    }

    public Optional<Hashable> findByPrototypeKey(UserEntity userEntity, String prototypeKey) {
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        return expenseCategoriesRepository.getByPrototypeKey(joyBasket, prototypeKey);
    }

    public Set<String> findAllUsedPrototypeKeys(UserEntity userEntity) {
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        return expenseCategoriesRepository.getAllUsedPrototypeKeys(joyBasket);
    }
}
