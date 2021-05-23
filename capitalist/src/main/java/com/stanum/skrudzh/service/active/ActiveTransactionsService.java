package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.controller.form.attributes.ActiveTransactionAttributes;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.ExpenseSourceEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.TransactionNatureEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceManagementService;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class ActiveTransactionsService extends TransactionBase {
    private final ExpenseSourceManagementService expenseSourceManagementService;

    private final ExpenseSourceFinder expenseSourceFinder;

    private final ExchangeService exchangeService;

    @Autowired
    public ActiveTransactionsService(EntityUtil entityUtil,
                                     UserUtil userUtil,
                                     CurrencyService currencyService,
                                     TransactionRepository transactionRepository,
                                     ExpenseSourceManagementService expenseSourceManagementService,
                                     ExpenseSourceFinder expenseSourceFinder,
                                     ExchangeService exchangeService) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.expenseSourceManagementService = expenseSourceManagementService;
        this.expenseSourceFinder = expenseSourceFinder;
        this.exchangeService = exchangeService;
    }

    public Set<TransactionEntity> getBuyTransactionsWithParams(ActiveEntity activeEntity, Timestamp from, Timestamp till) {
        return transactionRepository.getBuyActiveTransactionsWithParams(activeEntity.getId(), from, till);
    }

    public Set<TransactionEntity> getExpenseTransactionsWithParams(ActiveEntity activeEntity, Timestamp from, Timestamp till) {
        return transactionRepository.getExpenseTransactionsForActive(activeEntity.getId(), from, till);
    }

    public Set<TransactionEntity> getSaleTransactions(ActiveEntity activeEntity) {
        return transactionRepository.getSaleTransactionForActive(activeEntity.getId());
    }

    public Set<TransactionEntity> getAllOrderedByGotAtTransactions(ActiveEntity activeEntity) {
        return transactionRepository.getAllOrderedTransactionsForActive(activeEntity.getId());
    }

    public void createExpenseTransactionForActive(ActiveEntity activeEntity, ExpenseSourceEntity sourceEntity) {
        TransactionEntity expenseTransaction = new TransactionEntity();
        expenseTransaction.setUser(activeEntity.getBasketEntity().getUser());
        expenseTransaction.setSourceType(EntityTypeEnum.ExpenseSource.name());
        expenseTransaction.setSourceId(sourceEntity.getId());
        expenseTransaction.setDestinationType(EntityTypeEnum.Active.name());
        expenseTransaction.setDestinationId(activeEntity.getId());
        expenseTransaction.setAmountCents(activeEntity.getAlreadyPaidCents());
        expenseTransaction.setAmountCurrency(activeEntity.getCurrency());
        expenseTransaction.setConvertedAmountCents(activeEntity.getAlreadyPaidCents());
        expenseTransaction.setConvertedAmountCurrency(activeEntity.getCurrency());
        expenseTransaction.setGotAt(sourceEntity.getCreatedAt());
        expenseTransaction.setBuyingAsset(false);
        afterCreate(expenseTransaction);
        save(expenseTransaction);
    }

    public void createCostChangeTransaction(ActiveEntity activeEntity,
                                            BigDecimal costChange,
                                            ActiveTransactionAttributes activeTransactionAttributes,
                                            boolean isInitialTransaction) {
        ExpenseSourceEntity expenseSource;
        TransactionEntity costChangeTransaction;
        if (costChange.compareTo(BigDecimal.ZERO) > 0) {
            if (activeTransactionAttributes != null && activeTransactionAttributes.getSourceId() != null) {
                expenseSource = expenseSourceFinder.findById(activeTransactionAttributes.getSourceId());
            } else {
                expenseSource = expenseSourceFinder.findFirstByParams(activeEntity.getBasketEntity().getUser(),
                        true, activeEntity.getCurrency())
                        .orElseGet(() -> expenseSourceManagementService.createDefault(
                                activeEntity.getBasketEntity().getUser(),
                                true,
                                activeEntity.getCurrency()));
            }
            BigDecimal amountCents = exchangeService
                    .exchange(activeEntity.getCurrency(), expenseSource.getCurrency(), costChange);
            costChangeTransaction = createChangeTransaction(
                    new ChangeTransactionCF(
                            activeEntity.getBasketEntity().getUser(),
                            expenseSource,
                            activeEntity,
                            amountCents,
                            costChange,
                            true,
                            null,
                            null));
            if (isInitialTransaction) {
                costChangeTransaction.setActiveEntity(activeEntity);
                costChangeTransaction.setTransactionPurpose(TransactionPurposeEnum.creation);
                if (expenseSource.getIsVirtual()) {
                    costChangeTransaction.setTransactionNature(TransactionNatureEnum.system);
                }
            }
        } else {
            expenseSource = expenseSourceFinder.findFirstByParams(activeEntity.getBasketEntity().getUser(),
                    true, activeEntity.getCurrency())
                    .orElseGet(() -> expenseSourceManagementService.createDefault(activeEntity.getBasketEntity().getUser(),
                            true, activeEntity.getCurrency()));

            costChangeTransaction = createChangeTransaction(
                    new ChangeTransactionCF(
                            activeEntity.getBasketEntity().getUser(),
                            activeEntity,
                            expenseSource,
                            costChange.negate(),
                            costChange.negate(),
                            false,
                            null,
                            null));
        }
        save(costChangeTransaction);
    }

    public void bindTransactionForActive(ActiveEntity activeEntity, Long transactionId) {
        Optional<TransactionEntity> transactionOptional = transactionRepository.findById(transactionId);
        if(!transactionOptional.isPresent()) {
            log.error("Transaction with id={} not found", transactionId);
            throw new AppException(HttpAppError.NOT_FOUND);
        }
        TransactionEntity transaction = transactionOptional.get();
        transaction.setActiveEntity(activeEntity);
        transaction.setIsActiveSource(true);
        save(transaction);
    }

    public void updateAllProfits(ActiveEntity activeEntity) {
        Set<TransactionEntity> allActiveTransactions = getAllOrderedByGotAtTransactions(activeEntity);
        BigDecimal balance = BigDecimal.ZERO;
        BigDecimal invested = BigDecimal.ZERO;
        BigDecimal fullSaleProfit = BigDecimal.ZERO;
        for (TransactionEntity activeTransaction : allActiveTransactions) {
            if (isInvestTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getConvertedAmountCents());
                invested = invested.add(activeTransaction.getConvertedAmountCents());
            }
            if (isRefundIncTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getConvertedAmountCents());
                fullSaleProfit = balance.add(invested.negate());
            }
            if (isRefundDecTransaction(activeTransaction)) {
                balance = balance.add(activeTransaction.getAmountCents().negate());
                fullSaleProfit = balance.add(invested.negate());
            }
            if (isSaleTransaction(activeTransaction)) {
                BigDecimal sellAmount = activeTransaction.getAmountCents();
                if (invested.compareTo(sellAmount) >= 0 && balance.compareTo(sellAmount) > 0) {
                    invested = invested.add(sellAmount.negate());
                } else if (invested.compareTo(sellAmount) < 0) {
                    BigDecimal notEnough = sellAmount.add(invested.negate());
                    //notEnough amount from invested is taken from fullSaleProfit,
                    // so notEnough value is profit of sale transaction
                    if (notEnough.compareTo(BigDecimal.ZERO) >= 0 && balance.compareTo(sellAmount) > 0) {
                        activeTransaction.setProfit(notEnough);
                        save(activeTransaction);
                    } else if (balance.compareTo(sellAmount) == 0) {
                        activeTransaction.setProfit(fullSaleProfit);
                        save(activeTransaction);
                    }
                    invested = BigDecimal.ZERO;
                    fullSaleProfit = fullSaleProfit.add(notEnough.negate());
                } else if (invested.compareTo(sellAmount) >= 0 && balance.compareTo(sellAmount) <= 0) {
                    if (balance.compareTo(sellAmount) == 0) {
                        activeTransaction.setProfit(fullSaleProfit);
                        save(activeTransaction);
                        fullSaleProfit = BigDecimal.ZERO;
                    }
                    invested = invested.add(sellAmount.negate());
                }
                balance = balance.add(sellAmount.negate());
            }
        }
    }

    public boolean isInvestTransaction(TransactionEntity transactionEntity) {
        Object source = getSource(transactionEntity);
        return source instanceof ExpenseSourceEntity
                && (!((ExpenseSourceEntity) source).getIsVirtual()
                || ((ExpenseSourceEntity) source).getIsVirtual() && transactionEntity.getActiveEntity() != null)
                && transactionEntity.getBuyingAsset();
    }

    public boolean isRefundIncTransaction(TransactionEntity transactionEntity) {
        Object source = getSource(transactionEntity);

        if (transactionEntity.getActiveEntity()!=null) {
            return false;
        } else {
            return source instanceof ExpenseSourceEntity
                    && ((ExpenseSourceEntity) source).getIsVirtual()
                    && transactionEntity.getBuyingAsset()
                    || source instanceof IncomeSourceEntity;
        }
    }

    public boolean isRefundDecTransaction(TransactionEntity transactionEntity) {
        Object destination = getDestination(transactionEntity);
        return destination instanceof ExpenseSourceEntity
                && ((ExpenseSourceEntity) destination).getIsVirtual();
    }

    public boolean isSaleTransaction(TransactionEntity transactionEntity) {
        Object destination = getDestination(transactionEntity);
        return destination instanceof ExpenseSourceEntity
                && !((ExpenseSourceEntity) destination).getIsVirtual();
    }

    public boolean hasRegularUserTransactions(ActiveEntity activeEntity) {
        return !findRegularUserTransactions(activeEntity).isEmpty();
    }

    public Set<TransactionEntity> findRegularUserTransactions(ActiveEntity activeEntity) {
        return transactionRepository.getRegularUserTransactions(activeEntity.getId(), EntityTypeEnum.Active.name());
    }

    public Optional<TransactionEntity> findCreationTransaction(ActiveEntity activeEntity) {
        return transactionRepository.getActiveCreationTransaction(activeEntity.getId());
    }

    public Optional<Timestamp> getLastRegularTransactionGotAt(ActiveEntity activeEntity) {
        return transactionRepository.getLastRegularTransactionGotAt(activeEntity.getId(), EntityTypeEnum.Active.name());
    }

    public Optional<TransactionEntity> findSyncTransaction(ActiveEntity activeEntity) {
        Optional<TransactionEntity> syncTransactionOptional = findLastSyncTransaction(activeEntity);
        if (syncTransactionOptional.isPresent()) {
            if (hasRegularUserTransactions(activeEntity)) {
                Optional<Timestamp> lastRegularTransactionGotAtOptional = getLastRegularTransactionGotAt(activeEntity);
                if (lastRegularTransactionGotAtOptional.isPresent()) {
                    if (lastRegularTransactionGotAtOptional.get().before(syncTransactionOptional.get().getGotAt())) {
                        return syncTransactionOptional;
                    } else {
                        return Optional.empty();
                    }
                } else {
                    return syncTransactionOptional;
                }
            } else {
                return syncTransactionOptional;
            }
        } else {
            return Optional.empty();
        }
    }

    public BigDecimal getBalanceFromTransactions(ActiveEntity activeEntity) {
        BigDecimal balance = positiveTransactions(activeEntity)
                .stream()
                .map(TransactionEntity::getConvertedAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balance = balance.add(negativeTransactions(activeEntity)
                .stream()
                .map(TransactionEntity::getAmountCents)
                .reduce(BigDecimal.ZERO, BigDecimal::add).negate());
        return balance;
    }

    public Set<TransactionEntity> positiveTransactions(ActiveEntity activeEntity) {
        return transactionRepository.getPositiveActiveTransactions(activeEntity.getId());
    }

    public Set<TransactionEntity> negativeTransactions(ActiveEntity activeEntity) {
        return transactionRepository.getNegativeTransactions(activeEntity.getId(), EntityTypeEnum.Active.name());
    }

    private Optional<TransactionEntity> findLastSyncTransaction(ActiveEntity activeEntity) {
        return transactionRepository.getLastSyncTransaction(activeEntity.getId(), EntityTypeEnum.Active.name());
    }



}
