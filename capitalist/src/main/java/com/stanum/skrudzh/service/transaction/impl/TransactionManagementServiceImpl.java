package com.stanum.skrudzh.service.transaction.impl;

import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.model.base.Transactionable;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.active.ActiveCalculationService;
import com.stanum.skrudzh.service.active.ActiveTransactionsService;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.borrow.BorrowManagementService;
import com.stanum.skrudzh.service.borrow.BorrowTransactionService;
import com.stanum.skrudzh.service.credit.CreditManagementService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.hash.HashFinder;
import com.stanum.skrudzh.service.hash.HashManagementService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceManagementService;
import com.stanum.skrudzh.service.saltedge.SaltEdgeTransactionService;
import com.stanum.skrudzh.service.transaction.TransactionManagementService;
import com.stanum.skrudzh.service.transaction.TransactionValidationService;
import com.stanum.skrudzh.service.transaction.context.CreateTrContext;
import com.stanum.skrudzh.service.transaction.context.UpdateTrContext;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionManagementServiceImpl extends TransactionBase implements TransactionManagementService {

    private final CreditManagementService creditManagementService;

    private final BorrowManagementService borrowManagementService;

    private final IncomeSourceManagementService incomeSourceManagementService;

    private final BorrowTransactionService borrowTransactionService;

    private final ActiveTransactionsService activeTransactionsService;

    private final ActiveCalculationService activeCalculationService;

    private final SaltEdgeTransactionService saltEdgeTransactionService;

    private final BorrowFinder borrowFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    private final TransactionValidationService validationService;

    private final HashManagementService hashManagementService;

    private final HashFinder hashFinder;

    @Autowired
    public TransactionManagementServiceImpl(TransactionValidationService validationService,
                                        CreditManagementService creditManagementService,
                                        BorrowManagementService borrowManagementService,
                                        BorrowTransactionService borrowTransactionService,
                                        IncomeSourceManagementService incomeSourceManagementService,
                                        ActiveCalculationService activeCalculationService,
                                        ActiveTransactionsService activeTransactionsService,
                                        SaltEdgeTransactionService saltEdgeTransactionService,
                                        BorrowFinder borrowFinder,
                                        IncomeSourceFinder incomeSourceFinder,
                                        HashManagementService hashManagementService,
                                        HashFinder hashFinder,
                                        EntityUtil entityUtil,
                                        UserUtil userUtil,
                                        CurrencyService currencyService,
                                        TransactionRepository transactionRepository) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.validationService = validationService;
        this.creditManagementService = creditManagementService;
        this.borrowManagementService = borrowManagementService;
        this.borrowTransactionService = borrowTransactionService;
        this.incomeSourceManagementService = incomeSourceManagementService;
        this.activeCalculationService = activeCalculationService;
        this.activeTransactionsService = activeTransactionsService;
        this.borrowFinder = borrowFinder;
        this.incomeSourceFinder = incomeSourceFinder;
        this.hashManagementService = hashManagementService;
        this.hashFinder = hashFinder;
        this.saltEdgeTransactionService = saltEdgeTransactionService;
    }

    public TransactionEntity createTransaction(CreateTrContext context) {
        TransactionCreationForm.TransactionCF form = context.getForm();
        validationService.validateCreationForm(form);

        TransactionEntity transactionEntity = new TransactionEntity();
        checkAndFillTransactionWithCreationForm(form, transactionEntity);
        transactionEntity.setUser(context.getUserEntity());
        transactionEntity.setGotAt(Timestamp.valueOf(form.getGotAt()));
        if (isActiveSellTransaction(context.getSource(), context.getDestination())) {
            transactionEntity.setProfit(activeCalculationService.getProfit((ActiveEntity) context.getSource(), transactionEntity));
        }
        afterCreate(transactionEntity);
        save(transactionEntity);
        updateCreditIfNeeded(context.getDestination());
        updateBorrowByReturningTransactionIfNeeded(transactionEntity);
        updateActiveIfNeeded(transactionEntity);
        return transactionEntity;
    }

    public void updateSimilarTransaction(TransactionEntity transactionEntity, TransactionUpdatingForm.TransactionUF form) {
        log.info("Update similar transactions by id={}", transactionEntity.getId());
        long start = System.currentTimeMillis();

        Set<TransactionEntity> trs = new HashSet<>();
        if(EntityTypeEnum.ExpenseSource.name().equals(transactionEntity.getSourceType())) {
            trs = transactionRepository.findByUserAndCommentAndTransactionTypeAndDestinationTypeAndDestinationId(
                    transactionEntity.getUser(),
                    transactionEntity.getComment(),
                    transactionEntity.getTransactionType(),
                    transactionEntity.getDestinationType(),
                    transactionEntity.getDestinationId()
            );
        } else if(EntityTypeEnum.IncomeSource.name().equals(transactionEntity.getSourceType())) {
            trs = transactionRepository.findByUserAndCommentAndTransactionTypeAndSourceTypeAndSourceId(
                    transactionEntity.getUser(),
                    transactionEntity.getComment(),
                    transactionEntity.getTransactionType(),
                    transactionEntity.getSourceType(),
                    transactionEntity.getSourceId()
            );
        }
        trs = trs.stream().filter(tr -> !tr.getId().equals(transactionEntity.getId())).collect(Collectors.toSet());

        log.info("{} similar transactions found", trs.size());
        for(TransactionEntity tr : trs) {
            UpdateTrContext context = UpdateTrContext.builder()
                    .form(form)
                    .source(SerializationUtils.clone(tr))
                    .target(tr)
                    .updateSimilar(true)
                    .build();

            updateTransaction(context);
        }
        log.info("Update {} transactions took {} ms", trs.size(), System.currentTimeMillis() - start);
    }

    private void checkAmountChanged(UpdateTrContext context) {
        TransactionUpdatingForm.TransactionUF form = context.getForm();
        if (form.getAmountCents() != null || form.getConvertedAmountCents() != null) {
            context.setAmountChanged(true);
        }
        if (form.getBuyingAsset() != null && context.getTarget().getBuyingAsset() != form.getBuyingAsset()) {
            context.setBuyingAssetsChanged(true);
        }
    }

    private void updateDestination(UpdateTrContext context) {
        TransactionUpdatingForm.TransactionUF form = context.getForm();
        TransactionEntity transactionEntity = context.getTarget();

        if (form.getDestinationId() != null) transactionEntity.setDestinationId(form.getDestinationId());
        if (form.getDestinationType() != null) transactionEntity.setDestinationType(form.getDestinationType());
        if (form.getSourceId() != null) transactionEntity.setSourceId(form.getSourceId());
        if (form.getSourceType() != null) transactionEntity.setSourceType(form.getSourceType());
    }

    public void updateTransaction(UpdateTrContext context) {
        TransactionEntity source = context.getSource();
        TransactionEntity target = context.getTarget();

        validationService.validateUpdatingForm(context.getForm(), context.getTarget());
        checkAmountChanged(context);
        if(context.isUpdateSimilar()) {
            updateDestination(context);
        }
        else {
            checkAndUpdateTransactionWithUpdatingForm(context.getForm(), context.getTarget());
            if(context.getForm().getBorrowId() != null) {
                log.info("Bind transaction id={} with borrow id={}", source.getId(), context.getForm().getBorrowId());
                BorrowEntity borrow = borrowFinder.findById(context.getForm().getBorrowId());
                target.setReturningBorrow(borrow);
            }
        }

        afterUpdate(target, source.getSourceId(), source.getSourceType(),
                source.getDestinationId(), source.getDestinationType(),
                source.getAmountCents(), source.getConvertedAmountCents(), context.isBuyingAssetsChanged());
        updateHashesIfNeeded(new HashesUF(target,
                source.getSourceId(), source.getSourceType(), source.getDestinationId(), source.getDestinationType()));
        updateBorrow(target, context.isAmountChanged());
        updateBorrowByReturningTransactionIfNeeded(target);

        save(target);
        updateActiveProfitsIfNeeded(target);
        updateCreditIfNeeded(getDestination(target));
        updateActiveIfNeeded(target);
    }

    public void destroyTransaction(TransactionEntity transactionForDestroying) {
        if (isCreditingTransaction(transactionForDestroying)) {
            creditManagementService.destroyCredit(transactionForDestroying.getCredit(), false);
            return;
        }
        if (isBorrowingTransaction(transactionForDestroying)) {
            borrowManagementService.destroyBorrow(transactionForDestroying.getBorrow(), false);
            return;
        }
        transactionForDestroying.setDeletedAt(TimeUtil.now());
        afterDestroy(transactionForDestroying);
        save(transactionForDestroying);
        updateActiveProfitsIfNeeded(transactionForDestroying);
        updateCreditIfNeeded(getDestination(transactionForDestroying));
        updateBorrowByReturningTransactionIfNeeded(transactionForDestroying);
        updateActiveIfNeeded(transactionForDestroying);
    }

    public void duplicateTransaction(TransactionEntity transactionForDuplicate) {
        transactionForDuplicate.setIsDuplicated(true);
        transactionForDuplicate.setIsDuplicationActual(true);
        save(transactionForDuplicate);
        synchronizeTransactionsHistory(getSource(transactionForDuplicate), getDestination(transactionForDuplicate));
    }

    private void checkAndFillTransactionWithCreationForm(TransactionCreationForm.TransactionCF form,
                                                         TransactionEntity transactionEntity) {
        transactionEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
        transactionEntity.setAmountCurrency(form.getAmountCurrency() != null
                ? form.getAmountCurrency()
                : CurrencyService.usdIsoCode);
        transactionEntity.setBuyingAsset(form.getBuyingAsset() != null
                ? form.getBuyingAsset()
                : false);
        transactionEntity.setComment(form.getComment());
        transactionEntity.setConvertedAmountCents(BigDecimal.valueOf(form.getConvertedAmountCents()));
        transactionEntity.setConvertedAmountCurrency(form.getConvertedAmountCurrency() != null
                ? form.getConvertedAmountCurrency()
                : CurrencyService.usdIsoCode);
        transactionEntity.setDestinationId(form.getDestinationId());
        transactionEntity.setDestinationType(form.getDestinationType());
        transactionEntity.setSourceId(form.getSourceId());
        transactionEntity.setSourceType(form.getSourceType());
        BorrowEntity returningBorrow = form.getReturningBorrowId() != null
                ? borrowFinder.findById(form.getReturningBorrowId())
                : null;
        transactionEntity.setReturningBorrow(returningBorrow);
    }

    private void checkAndUpdateTransactionWithUpdatingForm(TransactionUpdatingForm.TransactionUF form, TransactionEntity transactionEntity) {
        if (form.getAmountCents() != null) {
            transactionEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
        }
        if (form.getAmountCurrency() != null) transactionEntity.setAmountCurrency(form.getAmountCurrency());
        if (form.getConvertedAmountCents() != null) {
            transactionEntity.setConvertedAmountCents(BigDecimal.valueOf(form.getConvertedAmountCents()));
        }
        if (form.getConvertedAmountCurrency() != null)
            transactionEntity.setConvertedAmountCurrency(form.getConvertedAmountCurrency());
        if (form.getComment() != null) transactionEntity.setComment(form.getComment());
        if (form.getDestinationId() != null) transactionEntity.setDestinationId(form.getDestinationId());
        if (form.getDestinationType() != null) transactionEntity.setDestinationType(form.getDestinationType());
        if (form.getSourceId() != null) transactionEntity.setSourceId(form.getSourceId());
        if (form.getSourceType() != null) transactionEntity.setSourceType(form.getSourceType());
        if (form.getGotAt() != null) transactionEntity.setGotAt(Timestamp.valueOf(form.getGotAt()));
        if (form.getBuyingAsset() != null && transactionEntity.getBuyingAsset() != form.getBuyingAsset()) {
            transactionEntity.setBuyingAsset(form.getBuyingAsset());
        }

        transactionEntity
                .setTransactionType(entityUtil
                        .defineTransactionType(getSource(transactionEntity), getDestination(transactionEntity), transactionEntity));
    }

    private void updateBorrow(TransactionEntity transactionEntity, boolean isAmountChanged) {
        if (isAmountChanged && transactionEntity.getBorrow() != null) {
            borrowManagementService.updateIsReturnedWithTransaction(transactionEntity.getBorrow(), transactionEntity);
        }
    }

    private void updateBorrowByReturningTransactionIfNeeded(TransactionEntity returningBorrowTransaction) {
        if (returningBorrowTransaction.getReturningBorrow() == null) return;
        BorrowEntity borrowEntity = returningBorrowTransaction.getReturningBorrow();
        if (borrowEntity != null) {
            TransactionEntity borrowingTransaction = borrowTransactionService.getBorrowingTransaction(borrowEntity);
            if (borrowingTransaction != null) {
                borrowManagementService.updateIsReturnedWithTransaction(borrowEntity, borrowingTransaction);
            }
        }
    }

    private void updateCreditIfNeeded(Object destination) {
        if (!(destination instanceof ExpenseCategoryEntity)) return;
        ExpenseCategoryEntity possibleCreditExpenseCategory = (ExpenseCategoryEntity) destination;
        CreditEntity creditEntity = possibleCreditExpenseCategory.getCreditEntity();
        if (creditEntity != null) {
            creditManagementService.updateIsPaid(creditEntity);
        }
    }

    private void updateActiveProfitsIfNeeded(TransactionEntity transactionEntity) {
        if (isActiveTransaction(transactionEntity)) {
            Object source = getSource(transactionEntity);
            Object destination = getDestination(transactionEntity);
            ActiveEntity activeEntity = source instanceof ActiveEntity
                    ? (ActiveEntity) source
                    : (ActiveEntity) destination;
            activeTransactionsService.updateAllProfits(activeEntity);
        }
    }

    private void updateActiveIfNeeded(TransactionEntity transactionEntity) {
        if (isActiveTransaction(transactionEntity)) {
            Object source = getSource(transactionEntity);
            Object destination = getDestination(transactionEntity);
            ActiveEntity activeEntity = source instanceof ActiveEntity
                    ? (ActiveEntity) source
                    : (ActiveEntity) destination;
            IncomeSourceEntity activeIncomeSource = incomeSourceFinder.findByActiveId(activeEntity.getId());
            if (activeIncomeSource != null) {
                incomeSourceManagementService
                        .updateIncomeSourceByActive(
                                activeIncomeSource,
                                activeEntity,
                                activeCalculationService.calculateMonthlyIncome(activeEntity));
            }
        }
    }

    private void updateHashesIfNeeded(HashesUF form) {
        TransactionEntity updatedTransaction = form.getTransactionEntity();
        if (isNeedToUpdateHash(form)) {
            Optional<Hashable> hashableOptional = getHashableFromTransaction(updatedTransaction);

            Optional<HashEntity> hashEntityOptional = Optional.empty();
            if (hashableOptional.isPresent()) {
                hashEntityOptional = hashFinder
                        .findHashByParams(
                                updatedTransaction.getUser(),
                                updatedTransaction.getSaltEdgeCategory(),
                                hashableOptional.get().getHashableType(),
                                hashableOptional.get().getCurrency());
            }
            if (hashEntityOptional.isPresent()) {
                Optional<HashEntity> finalHashEntity = hashEntityOptional;
                hashableOptional.ifPresent(hashable -> {
                    hashManagementService
                            .updateHashWithHashable(
                                    finalHashEntity.get(),
                                    hashable);
                    updatedTransaction.setIsAutoCategorized(false);
                });
            } else {
                hashableOptional.ifPresent(hashable ->
                    hashManagementService
                            .createUserHash(
                                    hashable,
                                    Constants.CATEGORY_DELIMITER + updatedTransaction.getSaltEdgeCategory(),
                                    updatedTransaction.getUser())
                );
            }

        }
    }

    private Boolean isActiveSellTransaction(Transactionable source, Transactionable destination) {
        return EntityTypeEnum.Active.equals(source.getEntityType()) &&
                (EntityTypeEnum.ExpenseSource.equals(destination.getEntityType()) && !((ExpenseSourceEntity) destination).getIsVirtual());
    }

    private Boolean isActiveTransaction(TransactionEntity transactionEntity) {
        return transactionEntity.getSourceType().equals(EntityTypeEnum.Active.name())
                || transactionEntity.getDestinationType().equals(EntityTypeEnum.Active.name());
    }

    private Boolean isCreditingTransaction(TransactionEntity transactionEntity) {
        return transactionEntity.getCredit() != null;
    }

    private Boolean isBorrowingTransaction(TransactionEntity transactionEntity) {
        return transactionEntity.getBorrow() != null;
    }

    private Boolean isNeedToUpdateHash(HashesUF form) {
        String currentDestinationType = form.getTransactionEntity().getDestinationType();
        String currentSourceType = form.getTransactionEntity().getSourceType();
        return form.getTransactionEntity().getSaltEdgeCategory() != null
                && ((currentSourceType.equals(EntityTypeEnum.ExpenseSource.name())
                && currentDestinationType.equals(EntityTypeEnum.ExpenseCategory.name()))
                && (!form.getOldDestinationType().equals(EntityTypeEnum.ExpenseCategory.name())
                    || !form.getOldDestinationId().equals(form.getTransactionEntity().getDestinationId()))
                || (currentSourceType.equals(EntityTypeEnum.IncomeSource.name())
                && currentDestinationType.equals(EntityTypeEnum.ExpenseSource.name()))
                && (!form.getOldSourceType().equals(EntityTypeEnum.IncomeSource.name())
                || !form.getOldSourceId().equals(form.getTransactionEntity().getSourceId())));
    }

    public Optional<Hashable> getHashableFromTransaction(TransactionEntity transactionEntity) {
        if (transactionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())) {
            return Optional.of(entityUtil.findHashable(
                    transactionEntity.getDestinationId(),
                    HashableTypeEnum.ExpenseCategory));
        } else if (transactionEntity.getDestinationType().equals(EntityTypeEnum.ExpenseSource.name())) {
            return Optional.of(entityUtil.findHashable(
                    transactionEntity.getSourceId(),
                    HashableTypeEnum.IncomeSource));
        }
        return Optional.empty();
    }

    private void synchronizeTransactionsHistory(Object source, Object destination) {
        if (entityUtil.hasAccountConnection(source)) saltEdgeTransactionService.synchronizeBalances(source);
        if (entityUtil.hasAccountConnection(destination)) saltEdgeTransactionService.synchronizeBalances(destination);
    }

    @Data
    @AllArgsConstructor
    private static class HashesUF {

        private TransactionEntity transactionEntity;

        private Long oldSourceId;

        private String oldSourceType;

        private Long oldDestinationId;

        private String oldDestinationType;

    }
}