package com.stanum.skrudzh.service.income_source.impl;

import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import com.stanum.skrudzh.localized_values.LocalizedValuesCache;
import com.stanum.skrudzh.model.enums.CreationTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceTransactionsService;
import com.stanum.skrudzh.service.income_source.IncomeSourceValidationService;
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
public class IncomeSourceManagementServiceImpl implements IncomeSourceManagementService {

    private final IncomeSourcesRepository incomeSourcesRepository;

    private final RowOrderUtil rowOrderUtil;

    private final ReminderService reminderService;

    private final IncomeSourceTransactionsService transactionService;

    private final IncomeSourceFinder incomeSourceFinder;

    private final TransactionableExampleFinder transactionableExampleFinder;

    private final ExpenseCategoryAndIncomeSource expenseCategoryAndIncomeSource;

    private final LocalizedValuesCache cache;

    public IncomeSourceEntity createIncomeSourceEntityWithForm(UserEntity userEntity, IncomeSourceCreationForm.IncomeSourceCF payload) {
        IncomeSourceEntity incomeSourceEntity = new IncomeSourceEntity();
        checkAndFillIncomeSourceWithCreatingForm(payload, incomeSourceEntity, userEntity);
        save(incomeSourceEntity);
        if (payload.getReminderAttributes() != null) {
            expenseCategoryAndIncomeSource.updateEntity(payload.getReminderAttributes(), incomeSourceEntity);
        }
        return incomeSourceEntity;
    }

    public IncomeSourceEntity createIncomeSourceEntityByUser(UserEntity userEntity, String currencyCode) {
        IncomeSourceEntity incomeSourceEntity = new IncomeSourceEntity();
        incomeSourceEntity.setUser(userEntity);
        incomeSourceEntity.setCurrency(currencyCode);
        incomeSourceEntity.setIsBorrow(false);
        incomeSourceEntity.setIsVirtual(false);
        rowOrderUtil.setLastPosition(EntityTypeEnum.IncomeSource, incomeSourceEntity);
        setCreationTimestamps(incomeSourceEntity);
        return incomeSourceEntity;
    }

    public IncomeSourceEntity createIncomeSourceByActive(ActiveEntity activeEntity) {
        IncomeSourceEntity incomeSourceEntity = new IncomeSourceEntity();
        incomeSourceEntity.setUser(activeEntity.getBasketEntity().getUser());
        incomeSourceEntity.setActive(activeEntity);
        incomeSourceEntity.setName(activeEntity.getName());
        incomeSourceEntity.setIconUrl(activeEntity.getIconUrl());
        incomeSourceEntity.setCurrency(activeEntity.getCurrency());
        incomeSourceEntity.setIsBorrow(false);
        incomeSourceEntity.setIsVirtual(false);
        rowOrderUtil.setLastPosition(EntityTypeEnum.IncomeSource, incomeSourceEntity);
        setCreationTimestamps(incomeSourceEntity);
        return incomeSourceEntity;
    }

    public void updateIncomeSourceWithForm(IncomeSourceEntity incomeSourceEntity,
                                           IncomeSourceUpdatingForm.IncomeSourceUF form,
                                           boolean patch) {
        if (form.getRowOrderPosition() != null) {
            log.info("Update order position for income source id = {}", incomeSourceEntity.getId());
            rowOrderUtil.updateRowOrderPosition(EntityTypeEnum.IncomeSource, form.getRowOrderPosition(), incomeSourceEntity);
        } else {
            checkAndUpdateIncomeSourceWithUpdatingForm(form, incomeSourceEntity, patch);
        }
        save(incomeSourceEntity);
    }

    public void destroyIncomeSource(IncomeSourceEntity incomeSourceEntity, boolean destroyTransactions) {
        if (destroyTransactions) {
            destroyTransactions(incomeSourceEntity);
        }
        incomeSourceEntity.setDeletedAt(TimeUtil.now());
        save(incomeSourceEntity);
    }

    public void updateIncomeSourceByActive(IncomeSourceEntity incomeSourceEntity,
                                           ActiveEntity activeEntity,
                                           BigDecimal newMonthlyPlanned) {
        boolean isNameChanged = false;
        boolean isIconChanged = false;

        if (!incomeSourceEntity.getName().equals(activeEntity.getName())) {
            incomeSourceEntity.setName(activeEntity.getName());
            isNameChanged = true;
        }
        if (incomeSourceEntity.getIconUrl() != null
                && !incomeSourceEntity.getIconUrl().equals(activeEntity.getIconUrl())) {
            incomeSourceEntity.setIconUrl(activeEntity.getIconUrl());
            isIconChanged = true;
        }

        updateMonthlyPlannedIncomeByActive(incomeSourceEntity, newMonthlyPlanned);
        afterUpdate(incomeSourceEntity, isNameChanged || isIconChanged);
    }

    public void updateMonthlyPlannedIncomeByActive(IncomeSourceEntity incomeSourceEntity,
                                                   BigDecimal newMonthlyPlanned) {
        incomeSourceEntity.setMonthlyPlannedCents(newMonthlyPlanned);
        incomeSourceEntity.setUpdatedAt(TimeUtil.now());
        save(incomeSourceEntity);
    }

    public void destroyActiveIncomeSource(ActiveEntity activeEntity, boolean destroyWithTransactions) {
        IncomeSourceEntity activeIncomeSource = incomeSourceFinder.findIncomeSourceByActive(activeEntity);
        if (activeIncomeSource != null) {
            if (destroyWithTransactions) {
                destroyTransactions(activeIncomeSource);
            }
            activeIncomeSource.setDeletedAt(TimeUtil.now());
            save(activeIncomeSource);
        }
    }

    public IncomeSourceEntity save(IncomeSourceEntity incomeSource) {
        return incomeSourcesRepository.save(incomeSource);
    }

    public String notificationLocKey() {
        return "INCOME_REMINDER_NOTIFICATION_MESSAGE_KEY";
    }

    public String[] notificationLocArgs(IncomeSourceEntity incomeSourceEntity) {
        ReminderEntity reminder = reminderService.findBySourceIdAndType(incomeSourceEntity.getId(), RemindableTypeEnum.IncomeSource);
        if (reminder != null) {
            return new String[]{incomeSourceEntity.getName(), reminder.getMessage() != null ? reminder.getMessage() : ""};
        } else {
            return new String[]{incomeSourceEntity.getName(), ""};
        }
    }

    public IncomeSourceEntity createBorrowIncomeSource(UserEntity userEntity, String currencyCode) {
        Locale locale = RequestUtil.getLocale();
        IncomeSourceEntity borrowIncomeSource = createIncomeSourceEntityByUser(userEntity, currencyCode);
        borrowIncomeSource.setIsBorrow(true);
        borrowIncomeSource.setName(cache.get("activerecord.defaults.models.transactionable_example.income_source.attributes.name.borrow", locale));
        borrowIncomeSource.setDescription(cache.get("activerecord.defaults.models.transactionable_example.income_source.attributes.description.borrow", locale));
        save(borrowIncomeSource);
        return borrowIncomeSource;
    }

    public IncomeSourceEntity createVirtualIncomeSource(UserEntity userEntity, String currencyCode) {
        IncomeSourceEntity virtualIncomeSource = createIncomeSourceEntityByUser(userEntity, currencyCode);
        virtualIncomeSource.setIsVirtual(true);
        virtualIncomeSource.setName(virtualIncomeSourceName());
        save(virtualIncomeSource);
        return virtualIncomeSource;
    }

    public Optional<IncomeSourceEntity> createIncomeSourceFromPrototypeWithTransaction(UserEntity userEntity,
                                                                                       String currencyCode,
                                                                                       String prototypeKey) {
        IncomeSourceEntity incomeSourceFromPrototype = createIncomeSourceEntityByUser(userEntity, currencyCode);
        Optional<TransactionableExampleEntity> prototypeOptional = transactionableExampleFinder
                .findTransactionableExpampleByPropertyKey(prototypeKey, RequestUtil.getRegion());
        if (prototypeOptional.isPresent()) {
            TransactionableExampleEntity prototype = prototypeOptional.get();
            incomeSourceFromPrototype.setPrototypeKey(prototypeKey);


            String name = cache.get(prototype.getNameKey());
            incomeSourceFromPrototype.setName(name != null ? name : incomeSourceFromTransactionName());
            incomeSourceFromPrototype.setDescription(cache.get(prototype.getDescriptionKey()));
            incomeSourceFromPrototype.setIconUrl(prototype.getIconUrl());
            incomeSourceFromPrototype.setCreationType(CreationTypeEnum.with_transaction);
            save(incomeSourceFromPrototype);
            return Optional.of(incomeSourceFromPrototype);
        } else {
            return Optional.empty();
        }
    }

    public void createDefaultIncomeSource(UserEntity userEntity, TransactionableExampleEntity incomeTransactionalEntity) {
        log.info("Create default income source name={}, user={}", incomeTransactionalEntity.getName(), userEntity.getId());
        IncomeSourceEntity incomeSourceEntity = createIncomeSourceEntityByUser(userEntity, userEntity.getDefaultCurrency());
        Locale locale = RequestUtil.getLocale();
        if(locale == null) {
            if(userEntity.getLocale() == null || userEntity.getLocale().isEmpty()) {
                log.error("Can't define locale for userId={}", userEntity.getId());
                return;
            }
            locale = new Locale(userEntity.getLocale());
        }
        incomeSourceEntity.setName(cache.get(incomeTransactionalEntity.getNameKey(), locale));
        incomeSourceEntity.setIconUrl(incomeTransactionalEntity.getIconUrl());
        incomeSourceEntity.setDescription(cache.get(incomeTransactionalEntity.getDescriptionKey(), locale));
        incomeSourceEntity.setPrototypeKey(incomeTransactionalEntity.getPrototypeKey());
        save(incomeSourceEntity);
    }

    private void checkAndFillIncomeSourceWithCreatingForm(IncomeSourceCreationForm.IncomeSourceCF form,
                                                          IncomeSourceEntity incomeSourceEntity,
                                                          UserEntity userEntity) {
        IncomeSourceValidationService.validateCreationForm(form);

        if (form.getPrototypeKey() != null) {
            Optional<TransactionableExampleEntity> prototypeOptional = transactionableExampleFinder
                    .findTransactionableExpampleByPropertyKey(form.getPrototypeKey(), RequestUtil.getRegion());
            prototypeOptional.ifPresentOrElse(prototype -> {
                incomeSourceEntity.setName(cache.get(prototype.getNameKey(), RequestUtil.getLocale()));
                incomeSourceEntity.setDescription(cache.get(prototype.getDescriptionKey(), RequestUtil.getLocale()));
                incomeSourceEntity.setIconUrl(prototype.getIconUrl());
                incomeSourceEntity.setPrototypeKey(form.getPrototypeKey());
            }, () -> {
                throw new AppException(HttpAppError.NOT_FOUND,
                        "TransactionableExampleEntity with prototypeKey (" + form.getPrototypeKey() + ") not found.");
            });
        }
        if (form.getName() != null) incomeSourceEntity.setName(form.getName());
        if (form.getDescription() != null) incomeSourceEntity.setDescription(form.getDescription());
        if (form.getIconUrl() != null) incomeSourceEntity.setIconUrl(form.getIconUrl());
        incomeSourceEntity.setCurrency(form.getCurrency() != null
                ? form.getCurrency()
                : userEntity.getDefaultCurrency());
        incomeSourceEntity.setMonthlyPlannedCents(form.getMonthlyPlannedCents() != null
                ? BigDecimal.valueOf(form.getMonthlyPlannedCents())
                : null);
        incomeSourceEntity.setUser(userEntity);
        if (form.getRowOrderPosition() != null) {
            rowOrderUtil.setRowOrderPosition(EntityTypeEnum.IncomeSource, form.getRowOrderPosition(), incomeSourceEntity);
        } else {
            if (form.getRowOrder() != null) {
                incomeSourceEntity.setRowOrder(form.getRowOrder());
            } else {
                rowOrderUtil.setLastPosition(EntityTypeEnum.IncomeSource, incomeSourceEntity);
            }
        }
        incomeSourceEntity.setIsBorrow(false);
        incomeSourceEntity.setIsVirtual(false);
        setCreationTimestamps(incomeSourceEntity);
    }

    private void checkAndUpdateIncomeSourceWithUpdatingForm(IncomeSourceUpdatingForm.IncomeSourceUF form,
                                                            IncomeSourceEntity incomeSourceEntity,
                                                            boolean patch) {
        IncomeSourceValidationService.validateUpdatingForm(form);
        boolean isNameChanged = false;
        boolean isIconChanged = false;
        if (form.getIconUrl() != null) {
            incomeSourceEntity.setIconUrl(form.getIconUrl());
            isIconChanged = true;
        }
        if (form.getMonthlyPlannedCents() != null)
            incomeSourceEntity.setMonthlyPlannedCents(BigDecimal.valueOf(form.getMonthlyPlannedCents()));
        else
            incomeSourceEntity.setMonthlyPlannedCents(null);
        if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(incomeSourceEntity.getName())) {
            incomeSourceEntity.setName(form.getName());
            isNameChanged = true;
        }
        if (form.getDescription() != null) {
            incomeSourceEntity.setDescription(form.getDescription());
        }
        if (form.getReminderAttributes() != null) {
            if (form.getReminderAttributes().getId() != null) {
                reminderService.saveUpdatedEntity(form.getReminderAttributes(), incomeSourceEntity);
            } else {
                reminderService.saveCreatedEntity(form.getReminderAttributes(), incomeSourceEntity);
            }
        }
        if(!patch || form.getPrototypeKey() != null) {
            incomeSourceEntity.setPrototypeKey(form.getPrototypeKey());
        }
        incomeSourceEntity.setUpdatedAt(TimeUtil.now());
        afterUpdate(incomeSourceEntity, isNameChanged || isIconChanged);
    }

    private void destroyTransactions(IncomeSourceEntity incomeSourceEntity) {
        Set<TransactionEntity> transactionEntities = transactionService.findAllIncomeSourceTransactions(incomeSourceEntity);
        Timestamp now = TimeUtil.now();
        transactionEntities.forEach(transactionEntity -> {
            transactionEntity.setDeletedAt(now);
            transactionService.afterDestroy(transactionEntity);
            transactionService.save(transactionEntity);
        });
    }

    private void afterUpdate(IncomeSourceEntity incomeSourceEntity, boolean isNeedToUpdateTransactions) {
        if (!isNeedToUpdateTransactions) return;
        Set<TransactionEntity> allIncomeSourceTransactions = transactionService.findAllIncomeSourceTransactions(incomeSourceEntity);
        allIncomeSourceTransactions.forEach(incomeSourceTransaction -> transactionService
                .updateSourceTransactionWithNameAndIcon(incomeSourceTransaction, incomeSourceEntity.getName(), incomeSourceEntity.getIconUrl()));
    }

    private void setCreationTimestamps(IncomeSourceEntity incomeSourceEntity) {
        Timestamp now = TimeUtil.now();
        incomeSourceEntity.setCreatedAt(now);
        incomeSourceEntity.setUpdatedAt(now);
    }

    private String virtualIncomeSourceName() {
        return ResourceBundle
                .getBundle(Constants.MESSAGES, RequestUtil.getLocale())
                .getString("activerecord.defaults.models.income_source.attributes.name.unknown");
    }

    private String incomeSourceFromTransactionName() {
        return ResourceBundle.getBundle(Constants.MESSAGES)
                .getString("activerecord.defaults.models.income_source.attributes.name.from_transaction");
    }

}
