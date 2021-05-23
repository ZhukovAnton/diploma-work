package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryUpdatingForm;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.ExpenseCategoryEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryRequestService {

    private final ExpenseCategoryFinder finder;

    private final ExpenseCategoryManagementService managementService;

    private final BasketFinder basketFinder;

    private final UserUtil userUtil;

    public Set<ExpenseCategoryEntity> indexExpenseCategoriesByBasket(Long basketId, boolean noBorrows) {
        userUtil.checkRightAccessAcrossBasket(basketId);
        return finder.findByBasketAndNoBorrow(basketId, noBorrows);
    }

    public Set<ExpenseCategoryEntity> indexExpenseCategoriesByUser(Long userId, boolean noBorrows) {
        userUtil.checkRightAccess(userId);
        return finder.findByUserAndNoBorrow(userId, noBorrows);
    }

    public ExpenseCategoryEntity getExpenseCategoryById(Long id) {
        ExpenseCategoryEntity expenseCategoryEntity = finder.findById(id);
        if(expenseCategoryEntity.getBasket() != null) {
            userUtil.checkRightAccess(expenseCategoryEntity.getBasket().getUser().getId());
        } else if(expenseCategoryEntity.getUser() != null) {
            userUtil.checkRightAccess(expenseCategoryEntity.getUser().getId());
        }
        return expenseCategoryEntity;
    }

    public ExpenseCategoryEntity getFirstBorrowExpenseCategory(Long basketId, String currencyCode) {
        BasketEntity basketEntity = basketFinder.findBasketById(basketId);
        userUtil.checkRightAccess(basketEntity.getUser().getId());
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(basketEntity.getUser(), BasketTypeEnum.joy);
        Optional<ExpenseCategoryEntity> expenseCategoryEntityOptional =
                finder.findBorrowExpenseCategoryByParams(joyBasket, currencyCode);
        return expenseCategoryEntityOptional.orElseGet(() -> managementService.createBorrowExpenseCategory(joyBasket, currencyCode));
    }

    public ExpenseCategoryEntity createExpenseCategoryWithForm(@Nullable Long userId, @Nullable Long basketId, ExpenseCategoryCreationForm.ExpenseCategoryCF form) {
        BasketEntity basketEntity = null;
        UserEntity userEntity = null;
        if(basketId != null) {
            basketEntity = basketFinder.findBasketById(basketId);
            userUtil.checkRightAccess(basketEntity.getUser().getId());
        } else {
            userUtil.checkRightAccess(userId);
            userEntity = RequestUtil.getUser();
        }
        return managementService.createExpenseCategoryWithForm(basketEntity, userEntity, form);
    }

    public void updateExpenseCategoryWithForm(Long id, ExpenseCategoryUpdatingForm.ExpenseCategoryUF form, boolean patch) {
        ExpenseCategoryEntity expenseCategoryEntity = getExpenseCategoryById(id);
        managementService.updateExpenseCategoryWithForm(expenseCategoryEntity, form, patch);
    }

    public void destroyExpenseCategory(Long id) {
        ExpenseCategoryEntity expenseCategoryEntity = getExpenseCategoryById(id);
        managementService.destroyExpenseCategory(expenseCategoryEntity, false);
    }

}
