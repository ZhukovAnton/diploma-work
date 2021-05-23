package com.stanum.skrudzh.utils.logic;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import com.stanum.skrudzh.jpa.repository.ExpenseCategoriesRepository;
import com.stanum.skrudzh.jpa.repository.ExpenseSourcesRepository;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RowOrderUtil {

    private final IncomeSourcesRepository incomeSourcesRepository;

    private final ExpenseSourcesRepository expenseSourcesRepository;

    private final ExpenseCategoriesRepository expenseCategoriesRepository;

    private final ActiveRepository activeRepository;

    public void setLastPosition(EntityTypeEnum sourceType, Rankable rankableEntity) {
        Integer lastRowOrderNumber;
        switch (sourceType) {
            case Active:
                lastRowOrderNumber = activeRepository
                        .getLastRowOrderNumber(((ActiveEntity) rankableEntity).getBasketEntity());
                rankableEntity.setRowOrder(lastRowOrderNumber != null
                        ? lastRowOrderNumber + 1
                        : 0);
                break;
            case IncomeSource:
                lastRowOrderNumber = incomeSourcesRepository
                        .getLastRowOrderNumber(((IncomeSourceEntity) rankableEntity).getUser());
                rankableEntity.setRowOrder(lastRowOrderNumber != null
                        ? lastRowOrderNumber + 1
                        : 0);
                break;
            case ExpenseSource:
                lastRowOrderNumber = expenseSourcesRepository
                        .getLastRowOrderNumber(((ExpenseSourceEntity) rankableEntity).getUser());
                rankableEntity.setRowOrder(lastRowOrderNumber != null
                        ? lastRowOrderNumber + 1
                        : 0);
                break;
            case ExpenseCategory:
                lastRowOrderNumber = expenseCategoriesRepository
                        .getLastRowOrderNumber(((ExpenseCategoryEntity) rankableEntity).getBasket());
                rankableEntity.setRowOrder(lastRowOrderNumber != null
                        ? lastRowOrderNumber + 1
                        : 0);
        }
    }

    public void setRowOrderPosition(EntityTypeEnum sourceType, Integer orderPosition, Object objectOrderFor) {
        switch (sourceType) {
            case Active:
                setOrderPositionForActive((ActiveEntity) objectOrderFor, orderPosition);
                break;
            case IncomeSource:
                setOrderPositionForIncomeSource((IncomeSourceEntity) objectOrderFor, orderPosition);
                break;
            case ExpenseSource:
                setOrderPositionForExpenseSource((ExpenseSourceEntity) objectOrderFor, orderPosition);
                break;
            case ExpenseCategory:
                setOrderPositionForExpenseCategory((ExpenseCategoryEntity) objectOrderFor, orderPosition);
                break;
        }
    }

    public void updateRowOrderPosition(EntityTypeEnum sourceType, Integer newOrderPosition, Object objectOrderFor) {
        switch (sourceType) {
            case Active:
                updateOrderPositionForActive((ActiveEntity) objectOrderFor, newOrderPosition);
                break;
            case IncomeSource:
                updateOrderPositionForIncomeSource((IncomeSourceEntity) objectOrderFor, newOrderPosition);
                break;
            case ExpenseSource:
                updateOrderPositionForExpenseSource((ExpenseSourceEntity) objectOrderFor, newOrderPosition);
                break;
            case ExpenseCategory:
                updateOrderPositionForExpenseCategory((ExpenseCategoryEntity) objectOrderFor, newOrderPosition);
                break;
        }
    }

    private void setOrderPositionForActive(ActiveEntity activeEntity, Integer orderPosition) {
        List<Rankable> actives = activeRepository.findAllRankable(activeEntity.getBasketEntity());
        setOrderPosition(actives, activeEntity, orderPosition);
        actives.forEach(rankable -> activeRepository.save((ActiveEntity) rankable));
    }

    private void setOrderPositionForIncomeSource(IncomeSourceEntity incomeSourceEntity, Integer orderPosition) {
        List<Rankable> incomes = incomeSourcesRepository.findAllByUserAndDeletedAtIsNullOrderByRowOrder(incomeSourceEntity.getUser());
        setOrderPosition(incomes, incomeSourceEntity, orderPosition);
        incomes.forEach(rankable -> incomeSourcesRepository.save((IncomeSourceEntity) rankable));
    }

    private void setOrderPositionForExpenseSource(ExpenseSourceEntity expenseSourceEntity, Integer orderPosition) {
        List<Rankable> expenses = expenseSourcesRepository.findAllRankableByUser(expenseSourceEntity.getUser());
        setOrderPosition(expenses, expenseSourceEntity, orderPosition);
        expenses.forEach(rankable -> expenseSourcesRepository.save((ExpenseSourceEntity) rankable));
    }

    private void setOrderPositionForExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity, Integer orderPosition) {
        List<Rankable> expenseCategories = expenseCategoriesRepository
                .findAllByBasketAndDeletedAtIsNullOrderByRowOrder(expenseCategoryEntity.getBasket());
        setOrderPosition(expenseCategories, expenseCategoryEntity, orderPosition);
        expenseCategories.forEach(rankable -> expenseCategoriesRepository.save((ExpenseCategoryEntity) rankable));
    }

    private void updateOrderPositionForActive(ActiveEntity activeEntity, Integer newOrderPosition) {
        List<Rankable> actives = activeRepository.findAllRankable(activeEntity.getBasketEntity());
        updateOrderPosition(actives, activeEntity, newOrderPosition);
        actives.forEach(rankable -> activeRepository.save((ActiveEntity) rankable));
    }

    private void updateOrderPositionForIncomeSource(IncomeSourceEntity incomeSourceEntity, Integer newOrderPosition) {
        List<Rankable> incomes = incomeSourcesRepository.findAllByUserAndDeletedAtIsNullOrderByRowOrder(incomeSourceEntity.getUser());
        updateOrderPosition(incomes, incomeSourceEntity, newOrderPosition);
        incomes.forEach(rankable -> incomeSourcesRepository.save((IncomeSourceEntity) rankable));
    }

    private void updateOrderPositionForExpenseSource(ExpenseSourceEntity expenseSourceEntity, Integer newOrderPosition) {
        List<Rankable> expenses = expenseSourcesRepository.findAllRankableByUser(expenseSourceEntity.getUser());
        updateOrderPosition(expenses, expenseSourceEntity, newOrderPosition);
        expenses.forEach(rankable -> expenseSourcesRepository.save((ExpenseSourceEntity) rankable));
    }

    private void updateOrderPositionForExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity, Integer newOrderPosition) {
        List<Rankable> expenseCategories = new ArrayList<>(expenseCategoriesRepository
                .getExpenseCategoryEntitiesByBasket(expenseCategoryEntity.getBasket().getId()));
        updateOrderPosition(expenseCategories, expenseCategoryEntity, newOrderPosition);
        expenseCategories.forEach(rankable -> expenseCategoriesRepository.save((ExpenseCategoryEntity) rankable));
    }

    private void setOrderPosition(List<Rankable> rankables, Rankable rankableEntity, Integer orderPosition) {
        if (orderPosition <= 0) {
            rankableEntity.setRowOrder(rankables.get(0).getRowOrder());
            rightShift(rankables, 0, rankables.size() - 1, true);
            return;
        }
        if (orderPosition >= rankables.size()) {
            rankableEntity.setRowOrder(rankables.get(rankables.size() - 1).getRowOrder() + 1);
            return;
        }
        rankableEntity.setRowOrder(rankables.get(orderPosition).getRowOrder());
        rightShift(rankables, orderPosition, rankables.size() - 1, true);
    }

    private void updateOrderPosition(List<Rankable> rankables, Rankable rankableEntity, Integer newOrderPosition) {
        Integer oldOrderPosition = rankables.indexOf(rankableEntity);
        if (oldOrderPosition.equals(newOrderPosition)) return;
        if (newOrderPosition <= 0) {
            Integer newRowOrder = rankables.get(0).getRowOrder();
            rightShift(rankables, 0, oldOrderPosition, false);
            rankableEntity.setRowOrder(newRowOrder);
            return;
        }
        if (newOrderPosition >= rankables.size()) {
            Integer newRowOrder = rankables.get(rankables.size() - 1).getRowOrder();
            leftShift(rankables, rankables.size() - 1, oldOrderPosition);
            rankableEntity.setRowOrder(newRowOrder);
            return;
        }
        Integer newRowOrder = rankables.get(newOrderPosition).getRowOrder();
        if (oldOrderPosition.compareTo(newOrderPosition) < 0) {
            leftShift(rankables, newOrderPosition, oldOrderPosition);
        } else {
            rightShift(rankables, newOrderPosition, oldOrderPosition, false);
        }
        rankableEntity.setRowOrder(newRowOrder);
    }

    private void rightShift(List<Rankable> rankables, Integer beginPosition, Integer endPosition, Boolean isCreate) {
        for (int i = beginPosition; i < endPosition; ++i) {
            rankables.get(i).setRowOrder(rankables.get(i + 1).getRowOrder());
        }
        Rankable lastEntity = rankables.get(endPosition);
        if (isCreate) {
            lastEntity.setRowOrder(lastEntity.getRowOrder() + 1);
        }
    }

    private void leftShift(List<Rankable> rankables, Integer beginPosition, Integer endPosition) {
        for (int i = beginPosition; i > endPosition; --i) {
            rankables.get(i).setRowOrder(rankables.get(i - 1).getRowOrder());
        }
    }
}
