package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.ExpenseCategoriesRepository;
import com.stanum.skrudzh.localized_values.LocalizedValuesCache;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.CreationTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.service.transactionable.TransactionableExampleFinder;
import com.stanum.skrudzh.utils.ExpenseCategoryAndIncomeSource;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.utils.logic.RowOrderUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategoryManagementService {

    private final ExpenseCategoriesRepository expenseCategoriesRepository;

    private final BasketFinder basketFinder;

    private final RowOrderUtil rowOrderUtil;

    private final ReminderService reminderService;

    private final ExpenseCategoryTransactionsService transactionService;

    private final ExpenseCategoryValidationService validationService;

    private final TransactionableExampleFinder transactionableExampleFinder;

    private final ExpenseCategoryAndIncomeSource expenseCategoryAndIncomeSource;

    private final LocalizedValuesCache cache;

    public ExpenseCategoryEntity createExpenseCategoryWithForm(BasketEntity basketEntity, UserEntity userEntity, ExpenseCategoryCreationForm.ExpenseCategoryCF form) {
        ExpenseCategoryEntity expenseCategoryEntity = new ExpenseCategoryEntity();
        expenseCategoryEntity.setBasket(basketEntity);
        expenseCategoryEntity.setUser(userEntity);
        checkAndFillExpenseCategoryWithCreationForm(expenseCategoryEntity, form);
        setCreationTimestamps(expenseCategoryEntity);
        //by default
        expenseCategoryEntity.setIsBorrow(false);
        expenseCategoryEntity.setIsVirtual(false);
        save(expenseCategoryEntity);
        if (form.getReminderAttributes() != null) {
            expenseCategoryAndIncomeSource.updateEntity(form.getReminderAttributes(), expenseCategoryEntity);
        }
        return expenseCategoryEntity;
    }

    public ExpenseCategoryEntity createExpenseCategoryEntityWithBasketAndCurrency(BasketEntity basketEntity, UserEntity userEntity, String currencyCode) {
        ExpenseCategoryEntity expenseCategoryEntity = new ExpenseCategoryEntity();
        expenseCategoryEntity.setCurrency(currencyCode);
        expenseCategoryEntity.setBasket(basketEntity);
        expenseCategoryEntity.setIsBorrow(false);
        expenseCategoryEntity.setIsVirtual(false);
        expenseCategoryEntity.setUser(userEntity);
        rowOrderUtil.setLastPosition(EntityTypeEnum.ExpenseCategory, expenseCategoryEntity);
        setCreationTimestamps(expenseCategoryEntity);
        return expenseCategoryEntity;
    }

    public void createExpenseCategoryForCredit(CreditEntity creditEntity) {
        ExpenseCategoryEntity expenseCategoryEntity = new ExpenseCategoryEntity();
        expenseCategoryEntity.setName(creditEntity.getName());
        expenseCategoryEntity.setCurrency(creditEntity.getCurrency());
        expenseCategoryEntity.setBasket(basketFinder.findBasketByUserAndType(creditEntity.getUser(), BasketTypeEnum.joy));
        expenseCategoryEntity.setIsBorrow(false);
        expenseCategoryEntity.setIsVirtual(false);
        expenseCategoryEntity.setCreditEntity(creditEntity);
        expenseCategoryEntity.setMonthlyPlannedCents(creditEntity.getMonthlyPaymentCents());
        rowOrderUtil.setLastPosition(EntityTypeEnum.ExpenseCategory, expenseCategoryEntity);
        setCreationTimestamps(expenseCategoryEntity);
        save(expenseCategoryEntity);
    }

    public void updateExpenseCategoryWithForm(ExpenseCategoryEntity expenseCategoryEntity,
                                              ExpenseCategoryUpdatingForm.ExpenseCategoryUF form,
                                              boolean patch) {
        if (form != null) {
            if (form.getRowOrderPosition() != null) {
                rowOrderUtil.updateRowOrderPosition(EntityTypeEnum.ExpenseCategory, form.getRowOrderPosition(), expenseCategoryEntity);
            } else {
                checkAndUpdateWithUpdatingForm(expenseCategoryEntity, form, patch);
            }
            if (form.getReminderAttributes() != null && form.getReminderAttributes().getId() != null) {
                reminderService.saveUpdatedEntity(form.getReminderAttributes(), expenseCategoryEntity);
            } else {
                reminderService.saveCreatedEntity(form.getReminderAttributes(), expenseCategoryEntity);
            }
        }
        expenseCategoryEntity.setUpdatedAt(TimeUtil.now());
        save(expenseCategoryEntity);
    }

    public void destroyExpenseCategory(ExpenseCategoryEntity expenseCategoryEntity, boolean destroyWithTransactions) {
        if (destroyWithTransactions) {
            Set<TransactionEntity> transactionsToDestroy =
                    transactionService.getExpenseTransactionsForExpenseCategory(expenseCategoryEntity);
            transactionsToDestroy.forEach(transactionEntity -> {
                transactionEntity.setDeletedAt(TimeUtil.now());
                transactionService.afterDestroy(transactionEntity);
                transactionService.save(transactionEntity);
            });
        }
        expenseCategoryEntity.setDeletedAt(TimeUtil.now());
        save(expenseCategoryEntity);
    }

    public ExpenseCategoryEntity save(ExpenseCategoryEntity expenseCategoryEntity) {
        return expenseCategoriesRepository.save(expenseCategoryEntity);
    }

    public void afterUpdate(ExpenseCategoryEntity expenseCategoryEntity, boolean isNeedToUpdateTransactions) {
        if (!isNeedToUpdateTransactions) return;
        Set<TransactionEntity> expenseCategoryTransactions = transactionService
                .getExpenseTransactionsForExpenseCategory(expenseCategoryEntity);
        expenseCategoryTransactions.forEach(expenseCategoryTransaction ->
                transactionService.updateDestinationTransactionWithNameAndIcon(
                        expenseCategoryTransaction,
                        expenseCategoryEntity.getName(),
                        expenseCategoryEntity.getIconUrl()));
    }

    public String notificationLocKey() {
        return "EXPENSE_REMINDER_NOTIFICATION_MESSAGE_KEY";
    }

    public String[] notificationLocArgs(ExpenseCategoryEntity expenseCategoryEntity) {
        ReminderEntity reminder = reminderService.findBySourceIdAndType(expenseCategoryEntity.getId(), RemindableTypeEnum.ExpenseCategory);
        if (reminder != null) {
            return new String[]{expenseCategoryEntity.getName(), reminder.getMessage() != null ? reminder.getMessage() : ""};
        } else {
            return new String[]{expenseCategoryEntity.getName(), ""};
        }
    }

    public ExpenseCategoryEntity createBorrowExpenseCategory(BasketEntity basketEntity, String currencyCode) {
        log.info("Create borrow ExpenseCategory for basket id={}, currencyCode{}", basketEntity.getId(), currencyCode);
        Locale locale = RequestUtil.getLocale();
        ExpenseCategoryEntity expenseCategoryEntity = createExpenseCategoryEntityWithBasketAndCurrency(basketEntity, basketEntity.getUser(), currencyCode);
        expenseCategoryEntity.setIsBorrow(true);
        expenseCategoryEntity.setName(cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.name.borrow", locale));
        expenseCategoryEntity.setDescription(cache.get("activerecord.defaults.models.transactionable_example.expense_category.attributes.description.borrow", locale));
        save(expenseCategoryEntity);
        return expenseCategoryEntity;
    }

    public ExpenseCategoryEntity createVirtualExpenseCategory(UserEntity userEntity, String currencyCode) {
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        ExpenseCategoryEntity virtualCategoryEntity = createExpenseCategoryEntityWithBasketAndCurrency(joyBasket, userEntity, currencyCode);
        virtualCategoryEntity.setIsVirtual(true);
        virtualCategoryEntity.setName(virtualExpenseCategoryName());
        save(virtualCategoryEntity);
        return virtualCategoryEntity;
    }

    public Optional<ExpenseCategoryEntity> createExpenseCategoryFromPrototypeWithTransaction(UserEntity userEntity, String currencyCode, String prototypeKey) {
        BasketEntity joyBasket = basketFinder.findBasketByUserAndType(userEntity, BasketTypeEnum.joy);
        ExpenseCategoryEntity expenseCategoryFromPrototype = createExpenseCategoryEntityWithBasketAndCurrency(joyBasket, userEntity, currencyCode);
        Optional<TransactionableExampleEntity> prototypeOptional = transactionableExampleFinder
                .findTransactionableExpampleByPropertyKey(prototypeKey, RequestUtil.getRegion());
        if (prototypeOptional.isPresent()) {
            TransactionableExampleEntity prototype = prototypeOptional.get();
            expenseCategoryFromPrototype.setPrototypeKey(prototypeKey);

            String name = cache.get(prototype.getNameKey());
            expenseCategoryFromPrototype.setName(name != null ? name : categoryFromTransactionName());
            expenseCategoryFromPrototype.setDescription(cache.get(prototype.getDescriptionKey()));
            expenseCategoryFromPrototype.setCreationType(CreationTypeEnum.with_transaction);
            expenseCategoryFromPrototype.setIconUrl(prototype.getIconUrl());
            save(expenseCategoryFromPrototype);
            return Optional.of(expenseCategoryFromPrototype);
        } else {
            return Optional.empty();
        }
    }

    private void checkAndFillExpenseCategoryWithCreationForm(ExpenseCategoryEntity expenseCategoryEntity, ExpenseCategoryCreationForm.ExpenseCategoryCF form) {
        validationService.validateCreationForm(form);

        if (form.getPrototypeKey() != null) {
            Optional<TransactionableExampleEntity> prototypeOptional = transactionableExampleFinder
                    .findTransactionableExpampleByPropertyKey(form.getPrototypeKey(), RequestUtil.getRegion());
            prototypeOptional.ifPresentOrElse(prototype -> {
                expenseCategoryEntity.setName(cache.get(prototype.getNameKey(), RequestUtil.getLocale()));
                expenseCategoryEntity.setDescription(cache.get(prototype.getDescriptionKey(), RequestUtil.getLocale()));
                expenseCategoryEntity.setIconUrl(prototype.getIconUrl());
                expenseCategoryEntity.setPrototypeKey(prototype.getPrototypeKey());
            }, () -> {
                throw new AppException(HttpAppError.NOT_FOUND,
                        "TransactionableExampleEntity with prototypeKey (" + form.getPrototypeKey() + ") not found.");
            });
        }
        if (form.getName() != null) expenseCategoryEntity.setName(form.getName());
        if (form.getDescription() != null) expenseCategoryEntity.setDescription(form.getDescription());
        if (form.getIconUrl() != null) expenseCategoryEntity.setIconUrl(form.getIconUrl());
        expenseCategoryEntity.setCurrency(form.getCurrency() != null
                ? form.getCurrency()
                : expenseCategoryEntity.getBasket().getUser().getDefaultCurrency());
        expenseCategoryEntity.setMonthlyPlannedCents(form.getMonthlyPlannedCents() != null
                ? BigDecimal.valueOf(form.getMonthlyPlannedCents())
                : null);
        if (form.getRowOrderPosition() != null) {
            rowOrderUtil.setRowOrderPosition(EntityTypeEnum.ExpenseCategory, form.getRowOrderPosition(), expenseCategoryEntity);
        } else {
            rowOrderUtil.setLastPosition(EntityTypeEnum.ExpenseCategory, expenseCategoryEntity);
        }
    }

    private void checkAndUpdateWithUpdatingForm(ExpenseCategoryEntity expenseCategoryEntity,
                                                ExpenseCategoryUpdatingForm.ExpenseCategoryUF form,
                                                boolean patch) {
        validationService.validateUpdatingForm(form);
        boolean isIconChanged = false;
        boolean isNameChanged = false;
        if (form.getIconUrl() != null && !form.getIconUrl().equals(expenseCategoryEntity.getIconUrl())) {
            expenseCategoryEntity.setIconUrl(form.getIconUrl());
            isIconChanged = true;
        }
        if (form.getMonthlyPlannedCents() != null) {
            expenseCategoryEntity.setMonthlyPlannedCents(BigDecimal.valueOf(form.getMonthlyPlannedCents()));
        }
        if (form.getDescription() != null) {
            expenseCategoryEntity.setDescription(form.getDescription());
        }
        if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(expenseCategoryEntity.getName())) {
            expenseCategoryEntity.setName(form.getName());
            isNameChanged = true;
        }
        if(!patch || form.getPrototypeKey() != null) {
            expenseCategoryEntity.setPrototypeKey(form.getPrototypeKey());
        }
        afterUpdate(expenseCategoryEntity, isIconChanged || isNameChanged);
    }

    private void setCreationTimestamps(ExpenseCategoryEntity expenseCategoryEntity) {
        Timestamp now = TimeUtil.now();
        expenseCategoryEntity.setCreatedAt(now);
        expenseCategoryEntity.setUpdatedAt(now);
    }

    private String virtualExpenseCategoryName() {
        return ResourceBundle
                .getBundle(Constants.MESSAGES, RequestUtil.getLocale())
                .getString("activerecord.defaults.models.expense_category.attributes.name.unknown");
    }

    private String categoryFromTransactionName() {
        return ResourceBundle.getBundle(Constants.MESSAGES)
                .getString("activerecord.defaults.models.expense_category.attributes.name.from_transaction");
    }

}
