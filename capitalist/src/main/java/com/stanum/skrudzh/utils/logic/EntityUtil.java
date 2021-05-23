package com.stanum.skrudzh.utils.logic;

import com.google.common.collect.Lists;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.exception.ValidationException;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.*;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import com.stanum.skrudzh.jpa.repository.ExpenseCategoriesRepository;
import com.stanum.skrudzh.jpa.repository.ExpenseSourcesRepository;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntityUtil {

    private final ExpenseSourcesRepository expenseSourcesRepository;

    private final IncomeSourcesRepository incomeSourcesRepository;

    private final ActiveRepository activeRepository;

    private final ExpenseCategoriesRepository expenseCategoriesRepository;

    public <T> void save(T entity) {
        if (entity instanceof ExpenseSourceEntity)
            expenseSourcesRepository.save((ExpenseSourceEntity) entity);
        if (entity instanceof ActiveEntity) activeRepository.save((ActiveEntity) entity);
        if (entity instanceof IncomeSourceEntity) incomeSourcesRepository.save((IncomeSourceEntity) entity);
    }

    public <T> void appendCents(T entityWithBalance, BigDecimal cents) {
        if (entityWithBalance instanceof ExpenseSourceEntity) {
            ExpenseSourceEntity expenseSourceEntity = (ExpenseSourceEntity) entityWithBalance;
            if (expenseSourceEntity.getAccountConnectionEntity() != null) return;
            expenseSourceEntity.setAmountCents(expenseSourceEntity.getAmountCents().add(cents));
        }
        if (entityWithBalance instanceof ActiveEntity) {
            ActiveEntity activeEntity = (ActiveEntity) entityWithBalance;
            if (activeEntity.getAccountConnectionEntity() != null) return;
            activeEntity.setCostCents(activeEntity.getCostCents().add(cents));
        }
        save(entityWithBalance);
    }

    public <T> void removeCents(T entityWithBalance, BigDecimal cents) {
        if (entityWithBalance instanceof ExpenseSourceEntity) {
            ExpenseSourceEntity expenseSourceEntity = (ExpenseSourceEntity) entityWithBalance;
            if (expenseSourceEntity.getAccountConnectionEntity() != null) return;
            expenseSourceEntity.setAmountCents(expenseSourceEntity.getAmountCents().add(cents.negate()));
        }
        if (entityWithBalance instanceof ActiveEntity) {
            ActiveEntity activeEntity = (ActiveEntity) entityWithBalance;
            if (activeEntity.getAccountConnectionEntity() != null) return;
            activeEntity.setCostCents(activeEntity.getCostCents().add(cents.negate()));
        }
        save(entityWithBalance);
    }

    public <T, V> void moveCents(T source, V destination, BigDecimal centsToRemove, BigDecimal centsToAppend) {
        removeCents(source, centsToRemove);
        appendCents(destination, centsToAppend);
    }

    public Hashable findHashable(Long id, HashableTypeEnum hashableType) {
        Optional<? extends Hashable> entity;
        switch (hashableType) {
            case IncomeSource:
                entity = incomeSourcesRepository.findById(id);
                break;
            case ExpenseCategory:
                entity =  expenseCategoriesRepository.findById(id);
                break;
            default:
                log.error("Can't find Hashable Type id={}, type={}", id, hashableType);
                throw new AppException(HttpAppError.NOT_FOUND);
        }
        return entity.orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND,
                "Can't find Hashable by id=" + id + " and type=" + hashableType));
    }

    public Transactionable find(Long id, EntityTypeEnum entityType) {
        Optional<? extends Transactionable> entity;
        switch (entityType) {
            case ExpenseSource:
                entity = expenseSourcesRepository.findById(id);
                break;
            case Active:
                entity = activeRepository.findById(id);
                break;
            case IncomeSource:
                entity = incomeSourcesRepository.findById(id);
                break;
            case ExpenseCategory:
                entity = expenseCategoriesRepository.findById(id);
                break;
            default:
                log.error("Can't define Transactionable EntityType for id={} and type={}", id, entityType);
                throw new AppException(HttpAppError.NOT_FOUND);
        }
        return entity.orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND,
                "Can't find Transactionable by id=" + id + " and type=" + entityType));
    }

    public BigDecimal getBalance(Object entity) {
        if (entity instanceof ExpenseSourceEntity) {
            return ((ExpenseSourceEntity) entity).getAmountCents();
        } else if (entity instanceof ActiveEntity) {
            return ((ActiveEntity) entity).getCostCents();
        } else {
            return null;
        }
    }

    public Timestamp getFetchDataFrom(Connectable entity) {
        Integer maxFetchInterval = entity.getMaxFetchInterval();
        LocalDateTime beginningTime = maxFetchInterval == null ? LocalDateTime.now(ZoneId.of("Z")) :
                LocalDateTime.now(ZoneId.of("Z")).minusDays(maxFetchInterval);
        return Timestamp.valueOf(beginningTime);
    }

    public TransactionTypeEnum defineTransactionType(Object source, Object destination, TransactionEntity transactionEntity) {
        //income
        if (source instanceof IncomeSourceEntity
                && destination instanceof ExpenseSourceEntity)
            return TransactionTypeEnum.income;
        //funds move
        if (source instanceof ExpenseSourceEntity
                && destination instanceof ExpenseSourceEntity && !isVirtual(destination))
            return TransactionTypeEnum.funds_move;
        if (source instanceof ExpenseSourceEntity
                && destination instanceof ActiveEntity
                && Boolean.TRUE.equals(transactionEntity.getBuyingAsset()))
            return TransactionTypeEnum.funds_move;
        if (source instanceof ActiveEntity
                && destination instanceof ExpenseSourceEntity)
            return TransactionTypeEnum.funds_move;
        if (source instanceof IncomeSourceEntity
                && destination instanceof ActiveEntity) {
            IncomeSourceEntity incomeSourceEntity = (IncomeSourceEntity) source;
            ActiveEntity activeEntity = (ActiveEntity) destination;
            if (incomeSourceEntity.getActive().getId().equals(activeEntity.getId())) {
                return TransactionTypeEnum.funds_move;
            }
        }
        //expense
        if (source instanceof ExpenseSourceEntity
                && destination instanceof ExpenseCategoryEntity)
            return TransactionTypeEnum.expense;
        if (source instanceof ExpenseSourceEntity
                && destination instanceof ExpenseSourceEntity && isVirtual(destination))
            return TransactionTypeEnum.expense;
        if (source instanceof ExpenseSourceEntity
                && destination instanceof ActiveEntity
                && Boolean.FALSE.equals(transactionEntity.getBuyingAsset()))
            return TransactionTypeEnum.expense;

        //if not defined. have not to be achievable
        Map<String, List<String>> errorMap = new HashMap<>();
        errorMap.put("transaction", Lists.newArrayList("Unknown type"));
        throw new ValidationException(HttpAppError.VALIDATION_FAILED, errorMap);
    }

    public boolean isAssociatedWithBasket(Object entity) {
        return entity instanceof ActiveEntity || entity instanceof ExpenseCategoryEntity;
    }

    public boolean isVirtual(Object entity) {
        if (entity instanceof IncomeSourceEntity) {
            return ((IncomeSourceEntity) entity).getIsVirtual();
        } else if (entity instanceof ExpenseSourceEntity) {
            return ((ExpenseSourceEntity) entity).getIsVirtual();
        } else if (entity instanceof ExpenseCategoryEntity) {
            return ((ExpenseCategoryEntity) entity).getIsVirtual();
        } else {
            return false;
        }
    }

    public boolean isBorrowOrReturn(Object entity) {
        if (entity instanceof IncomeSourceEntity) {
            return ((IncomeSourceEntity) entity).getIsBorrow();
        } else if (entity instanceof ExpenseCategoryEntity) {
            return ((ExpenseCategoryEntity) entity).getIsBorrow();
        } else {
            return false;
        }
    }

    public boolean hasAccountConnection(Object accountHolder) {
        if (accountHolder instanceof ExpenseSourceEntity) {
            return ((ExpenseSourceEntity) accountHolder).getAccountConnectionEntity() != null;
        } else if (accountHolder instanceof ActiveEntity) {
            return ((ActiveEntity) accountHolder).getAccountConnectionEntity() != null;
        } else {
            return false;
        }
    }

    public BasketEntity getBasketAssociatedWithEntity(Object entity) {
        if (entity instanceof ActiveEntity) {
            return ((ActiveEntity) entity).getBasketEntity();
        }
        if (entity instanceof ExpenseCategoryEntity) {
            return ((ExpenseCategoryEntity) entity).getBasket();
        }
        return null;
    }

}
