package com.stanum.skrudzh.service.common.impl;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.active.ActiveManagementService;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.borrow.BorrowManagementService;
import com.stanum.skrudzh.service.common.CommonService;
import com.stanum.skrudzh.service.credit.CreditFinder;
import com.stanum.skrudzh.service.credit.CreditManagementService;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionFinder;
import com.stanum.skrudzh.service.saltedge.connection.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final CreditManagementService creditManagementService;

    private final BorrowManagementService borrowManagementService;

    private final ActiveManagementService activeManagementService;

    private final ConnectionService connectionService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final CreditFinder creditFinder;

    private final BorrowFinder borrowFinder;

    private final ActiveFinder activeFinder;

    private final ConnectionFinder connectionFinder;

    private final BasketFinder basketFinder;

    @Override
    public void destroyUsersData(UserEntity userEntity) {
        log.info("Destroy user data for userId={}", userEntity.getId());
        destroyConnections(userEntity);
        destroyActive(userEntity);
        destroyBorrows(userEntity);
        destroyCredits(userEntity);
        destroyIncomeSources(userEntity);
        destroyExpenseCategories(userEntity);
        destroyExpenseSources(userEntity);
    }

    private void destroyConnections(UserEntity userEntity) {
        log.info("Destroy connections for userId={}", userEntity.getId());
        Set<ConnectionEntity> connectionEntities = connectionFinder.findAllUsersConnections(userEntity);
        connectionEntities.forEach(connection -> connectionService
                .destroyConnection(connection, true));
    }

    private void destroyIncomeSources(UserEntity userEntity) {
        log.info("Destroy income sources for userId={}", userEntity.getId());
        Set<IncomeSourceEntity> incomeSourceEntities = incomeSourceFinder.findAllByUser(userEntity);
        incomeSourceEntities.forEach(incomeSource -> incomeSourceManagementService
                .destroyIncomeSource(incomeSource, true));
    }

    private void destroyExpenseCategories(UserEntity userEntity) {
        log.info("Destroy expense categories for userId={}", userEntity.getId());
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        Set<ExpenseCategoryEntity> expenseCategoryEntities = expenseCategoryFinder.findAllByBasket(joyBasket);
        expenseCategoryEntities.forEach(expenseCategory -> expenseCategoryManagementService
                .destroyExpenseCategory(expenseCategory, true));
    }

    private void destroyExpenseSources(UserEntity userEntity) {
        log.info("Destroy expense sources for userId={}", userEntity.getId());
        Set<ExpenseSourceEntity> expenseSourceEntities = expenseSourceFinder.findAllByUserEntity(userEntity);
        expenseSourceEntities.forEach(expenseSource -> expenseSourceManagementService
                .destroy(expenseSource, true, true));
    }

    private void destroyBorrows(UserEntity userEntity) {
        log.info("Destroy borrows for userId={}", userEntity.getId());
        Set<BorrowEntity> borrowEntities = borrowFinder.findAllByUserEntity(userEntity);
        borrowEntities.forEach(borrow -> borrowManagementService
                .destroyBorrow(borrow, true));
    }

    private void destroyCredits(UserEntity userEntity) {
        log.info("Destroy credits for userId={}", userEntity.getId());
        Set<CreditEntity> creditEntities = creditFinder.findAllByUser(userEntity);
        creditEntities.forEach(credit -> creditManagementService
                .destroyCredit(credit, true));
    }

    private void destroyActive(UserEntity userEntity) {
        log.info("Destroy actives for userId={}", userEntity.getId());
        Set<ActiveEntity> activeEntities = activeFinder.findAllByUserEntity(userEntity);
        activeEntities.forEach(active -> activeManagementService
                .destroyActive(active, true));
    }
}
