package com.stanum.skrudzh.service.onboarding;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.repository.HashExampleRepository;
import com.stanum.skrudzh.jpa.repository.TransactionableExampleRepository;
import com.stanum.skrudzh.localized_values.LocalizedValuesCache;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryManagementService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.service.hash.HashManagementService;
import com.stanum.skrudzh.service.hashable.HashableFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.user.UserManagementService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OnboardingService {

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final ExpenseCategoryManagementService expenseCategoryManagementService;

    private final TransactionableExampleRepository transactionableExampleRepository;

    private final HashExampleRepository hashExampleRepository;

    private final HashManagementService hashManagementService;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    private final BasketFinder basketFinder;

    private final HashableFinder hashableFinder;

    private final UserManagementService userManagementService;

    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final LocalizedValuesCache cache;

    public void onboarding(UserEntity userEntity) {
        if (userEntity.getOnBoarded()) {
            log.warn("User {} is already onboarded", userEntity.getId());
            return;
        }
        log.info("Start onboarding, userId={}", userEntity.getId());
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);

        String region = RequestUtil.getRegion();
        if(region == null) {
            region = Constants.DEFAULT_REGION;
        }

        fillIncomeSources(userEntity, region);
        fillExpenseCategories(userEntity, joyBasket, region);
        fillHashes(userEntity, region);
        fillExpenseSources(userEntity, region);
        createDefaultBorrowIncome(userEntity);
        createBorrowExpenseCategory(userEntity, joyBasket);

        userEntity.setOnBoarded(true);
        userManagementService.save(userEntity);
        log.info("Onboarding finished successfully, userId={}", userEntity.getId());
    }

    private void fillExpenseSources(UserEntity userEntity, String region) {
        log.info("Create default income sources for user {}, region = {}", userEntity.getId(), region);

        List<TransactionableExampleEntity> expenseTransactionalEntities = transactionableExampleRepository
                .findAllByTypeCountryAndCreateByDefault(
                        EntityTypeEnum.ExpenseSource.name(),
                        true,
                        region);

        expenseTransactionalEntities = expenseTransactionalEntities.stream()
                .sorted(Comparator.comparing(Rankable::getRowOrder)).collect(Collectors.toList());

        expenseTransactionalEntities.forEach(ex -> {
                    log.info("Create default expense source by template id={}, " +
                            "name = {} for userId={}", ex.getId(), ex.getName(), userEntity.getId());
                    expenseSourceManagementService.createDefault(
                            ex,
                            userEntity,
                            false,
                            userEntity.getDefaultCurrency(),
                            cache.get(ex.getNameKey(), RequestUtil.getLocale())
                    );
                }
        );
    }

    private void createDefaultBorrowIncome(UserEntity userEntity) {
        incomeSourceFinder.findBorrowIncomeSource(userEntity, userEntity.getDefaultCurrency())
                .orElseGet(() -> {
                    log.info("Create default borrow income for userId={}", userEntity.getId());
                    return incomeSourceManagementService.createBorrowIncomeSource(userEntity, userEntity.getDefaultCurrency());
                });
    }

    private void createBorrowExpenseCategory(UserEntity userEntity, BasketEntity joyBasket) {
        expenseCategoryFinder.findBorrowExpenseCategoryByParams(joyBasket, userEntity.getDefaultCurrency())
                .orElseGet(() -> {
                    log.info("Create default borrow expense category for userId={}", userEntity.getId());
                    return expenseCategoryManagementService.createBorrowExpenseCategory(joyBasket, userEntity.getDefaultCurrency());
                });
    }

    private void fillIncomeSources(UserEntity userEntity, String region) {
        log.info("Create default income sources for user {}", userEntity.getId());
        List<TransactionableExampleEntity> incomeTransactionalEntities = transactionableExampleRepository
                .findAllByTypeCountryAndCreateByDefault(
                        EntityTypeEnum.IncomeSource.name(),
                        true,
                        region);

        incomeTransactionalEntities.forEach(incomeTransactionalEntity -> {
            incomeSourceManagementService.createDefaultIncomeSource(userEntity, incomeTransactionalEntity);
        });
    }

    private void fillExpenseCategories(UserEntity userEntity, BasketEntity joyBasket, String region) {
        log.info("Create expense categories for user {}, region={}", userEntity.getId(), region);
        List<TransactionableExampleEntity> expenseTransactionalEntities = transactionableExampleRepository
                .findAllByTypeCountryAndCreateByDefault(
                        EntityTypeEnum.ExpenseCategory.name(),
                        true,
                        region);

        expenseTransactionalEntities.forEach(expenseTransactionalEntity -> {
            ExpenseCategoryEntity expenseCategoryEntity = expenseCategoryManagementService
                    .createExpenseCategoryEntityWithBasketAndCurrency(joyBasket, userEntity, userEntity.getDefaultCurrency());
            expenseCategoryEntity.setName(cache.get(expenseTransactionalEntity.getNameKey(), RequestUtil.getLocale()));
            expenseCategoryEntity.setDescription(
                    cache.get(expenseTransactionalEntity.getDescriptionKey(), RequestUtil.getLocale()));
            expenseCategoryEntity.setIconUrl(expenseTransactionalEntity.getIconUrl());
            expenseCategoryEntity.setPrototypeKey(expenseTransactionalEntity.getPrototypeKey());
            expenseCategoryManagementService.save(expenseCategoryEntity);
        });
    }

    private void fillHashes(UserEntity userEntity, String region) {
        log.info("Fill hashes for user {}, region={}", userEntity.getId(), region);
        Set<HashExampleEntity> hashExampleEntities = hashExampleRepository
                .findAllByCountry(region);
        hashExampleEntities.forEach(hashExample -> {
            Optional<Hashable> hashableOptional = hashableFinder
                    .findHashable(
                            hashExample.getHashableType(),
                            hashExample.getPrototypeKey(),
                            userEntity);
            if (hashableOptional.isPresent()) {
                hashManagementService.createHash(
                        hashableOptional.get(),
                        hashExample,
                        userEntity);
            } else {
                hashManagementService.createHash(
                        hashExample,
                        userEntity);
            }
        });
    }
}
