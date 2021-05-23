package com.stanum.skrudzh.service;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.model.base.Base;
import com.stanum.skrudzh.jpa.model.base.HasNameAndIcon;
import com.stanum.skrudzh.jpa.model.base.Transactionable;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.SaltEdgeTransactionStatusEnum;
import com.stanum.skrudzh.model.enums.TransactionNatureEnum;
import com.stanum.skrudzh.model.enums.TransactionPurposeEnum;
import com.stanum.skrudzh.saltage.model.Transaction;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.transaction.context.CreateTrContext;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;


@RequiredArgsConstructor
@Data
@Service
@Slf4j
public abstract class TransactionBase {

    protected final TransactionRepository transactionRepository;

    protected final EntityUtil entityUtil;

    protected final UserUtil userUtil;

    protected final CurrencyService currencyService;

    public TransactionEntity createTransactionEntityFromSaltEdgeTransaction(Transaction saltEdgeTransaction,
                                                                            AccountEntity accountEntity) {
        TransactionEntity transactionEntity = new TransactionEntity();
        Currency transactionCurrency = CurrencyService.getCurrencyByIsoCode(saltEdgeTransaction.getCurrencyCode());
        BigDecimal amountCents = saltEdgeTransaction.getAmount()
                .multiply(BigDecimal.valueOf(transactionCurrency.getSubunitToUnit()));
        transactionEntity.setSaltEdgeTransactionId(saltEdgeTransaction.getId());
        transactionEntity.setSaltEdgeTransactionStatus(saltEdgeTransaction.getStatus() != null
                ? SaltEdgeTransactionStatusEnum
                .valueOf(saltEdgeTransaction.getStatus())
                : null);
        transactionEntity.setUser(accountEntity.getConnectionEntity().getUser());
        transactionEntity.setAccountEntity(accountEntity);
        transactionEntity.setGotAt(getGotAtFromSaltEdgeTransaction(saltEdgeTransaction));
        transactionEntity.setBuyingAsset(false);
        transactionEntity.setAmountCurrency(saltEdgeTransaction.getCurrencyCode());
        transactionEntity.setConvertedAmountCurrency(saltEdgeTransaction.getCurrencyCode());
        transactionEntity.setComment(saltEdgeTransaction.getDescription());
        transactionEntity.setTransactionNature(TransactionNatureEnum.bank);
        if (saltEdgeTransaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            transactionEntity.setAmountCents(amountCents);
            transactionEntity.setConvertedAmountCents(amountCents);
        } else {
            transactionEntity.setAmountCents(amountCents.negate());
            transactionEntity.setConvertedAmountCents(amountCents.negate());
        }
        return transactionEntity;
    }

    public boolean hasTransactions(Long entityId, String entityType) {
        return !transactionRepository.findAllBySourceOrDestination(entityId, entityType).isEmpty();
    }

    public TransactionEntity createChangeTransaction(ChangeTransactionCF form) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUser(form.getUserEntity());

        transactionEntity.setSourceType(form.getSource().getEntityType().name());
        transactionEntity.setSourceId(form.getSource().getId());

        transactionEntity.setDestinationType(form.getDestination().getEntityType().name());
        transactionEntity.setDestinationId(form.getDestination().getId());

        transactionEntity.setAmountCents(form.getAmountOfCents());
        transactionEntity.setConvertedAmountCents(form.getConvertedAmountOfCents());
        transactionEntity.setAmountCurrency(form.getSource().getCurrency());
        transactionEntity.setConvertedAmountCurrency(form.getDestination().getCurrency());

        transactionEntity.setGotAt(TimeUtil.now());
        transactionEntity.setBuyingAsset(form.isBuyingAsset());

        if (form.getTransactionPurpose() != null)
            transactionEntity.setTransactionPurpose(form.getTransactionPurpose());
        if (form.getTransactionNature() != null)
            transactionEntity.setTransactionNature(form.getTransactionNature());

        afterCreate(transactionEntity);

        return transactionEntity;
    }

    public TransactionEntity save(TransactionEntity transactionEntity) {
        log.debug("Save transaction id={}, userId={}", transactionEntity.getId(), transactionEntity.getUser().getId());
        return transactionRepository.save(transactionEntity);
    }

    public Set<TransactionEntity> findRegularUserTransactions(Transactionable entity) {
        return transactionRepository.getRegularUserTransactions(entity.getId(), entity.getEntityType().name());
    }

    public Optional<Timestamp> findLastRegularTransactionGotAt(Transactionable entity) {
        return transactionRepository.getLastRegularTransactionGotAt(entity.getId(), entity.getEntityType().name());
    }

    public Optional<TransactionEntity> findLastSyncTransaction(Transactionable entity) {
        Long entityId = entity.getId();
        String entityType = entity.getEntityType().name();
        log.info("Find last sync transaction for entityId={}, entityType={}", entityId, entityType);
        return transactionRepository.getLastSyncTransaction(entityId, entityType);
    }

    public void updateSourceTransactionWithNameAndIcon(TransactionEntity transactionEntity, String newTitle, String newIconUrl) {
        transactionEntity.setSourceTitle(newTitle);
        transactionEntity.setSourceIconUrl(newIconUrl);
        save(transactionEntity);
    }

    public void updateDestinationTransactionWithNameAndIcon(TransactionEntity transactionEntity, String newTitle, String newIconUrl) {
        transactionEntity.setDestinationTitle(newTitle);
        transactionEntity.setDestinationIconUrl(newIconUrl);
        save(transactionEntity);
    }

    public void afterCreate(TransactionEntity transactionEntity) {
        Transactionable source = getSource(transactionEntity);
        Transactionable destination = getDestination(transactionEntity);
        if (transactionEntity.getSourceType().equals(EntityTypeEnum.IncomeSource.name())) {
            entityUtil.appendCents(destination, transactionEntity.getConvertedAmountCents());
        }
        if (isSourceReduceDestinationGrowth(transactionEntity)) {
            entityUtil.moveCents(source,
                    destination,
                    transactionEntity.getAmountCents(),
                    transactionEntity.getConvertedAmountCents());
        }
        if (isSourceReduce(transactionEntity)) {
            entityUtil.removeCents(source, transactionEntity.getAmountCents());
        }
        updateTitlesAndIconUrls(transactionEntity, source, destination);
        transactionEntity.setTransactionType(entityUtil.defineTransactionType(source, destination, transactionEntity));
        if (entityUtil.isAssociatedWithBasket(destination)) {
            BasketEntity basketEntity = entityUtil.getBasketAssociatedWithEntity(destination);
            transactionEntity.setBasketType(basketEntity.getBasketType());
        }
        setFlags(transactionEntity, source, destination);
    }

    public void afterUpdate(TransactionEntity transactionEntity,
                            Long oldSourceId,
                            String oldSourceType,
                            Long oldDestinationId,
                            String oldDestinationType,
                            BigDecimal oldAmount,
                            BigDecimal oldConvertedAmount,
                            boolean isBuyingAssetsChanged) {
        Transactionable source = getSource(transactionEntity);
        Transactionable destination = getDestination(transactionEntity);
        updateTitlesAndIconUrls(transactionEntity, source, destination);
        updateSourceAndDestinationBalances(transactionEntity, oldSourceId, oldSourceType, oldDestinationId, oldDestinationType, oldAmount, oldConvertedAmount, isBuyingAssetsChanged);
        setFlags(transactionEntity, source, destination);
    }

    public void afterDestroy(TransactionEntity transactionEntity) {
        Transactionable source = getSource(transactionEntity);
        Transactionable destination = getDestination(transactionEntity);
        if (EntityTypeEnum.IncomeSource == source.getEntityType()) {
            entityUtil.removeCents(destination, transactionEntity.getConvertedAmountCents());
        }
        if (isSourceReduceDestinationGrowth(transactionEntity)) {
            entityUtil.appendCents(source, transactionEntity.getAmountCents());
            entityUtil.removeCents(destination, transactionEntity.getConvertedAmountCents());
        }
        if (isSourceReduce(transactionEntity)) {
            entityUtil.appendCents(source, transactionEntity.getAmountCents());
        }
    }

    protected Transactionable getSource(TransactionEntity transactionEntity) {
        return entityUtil.find(transactionEntity.getSourceId(), EntityTypeEnum.valueOf(transactionEntity.getSourceType()));
    }

    protected Transactionable getDestination(TransactionEntity transactionEntity) {
        return entityUtil.find(transactionEntity.getDestinationId(), EntityTypeEnum.valueOf(transactionEntity.getDestinationType()));
    }

    protected Optional<Timestamp> getLastRegularTransactionGotAt(Object transactionHolder) {
        if (transactionHolder instanceof ExpenseSourceEntity) {
            return transactionRepository.getLastRegularTransactionGotAt(
                    ((ExpenseSourceEntity) transactionHolder).getId(),
                    EntityTypeEnum.ExpenseSource.name());
        } else if (transactionHolder instanceof ActiveEntity) {
            return transactionRepository.getLastRegularTransactionGotAt(
                    ((ActiveEntity) transactionHolder).getId(),
                    EntityTypeEnum.Active.name());
        } else {
            return Optional.empty();
        }
    }

    protected Boolean isSourceReduceDestinationGrowth(TransactionEntity transactionEntity) {
        boolean sourceReduceDestGrowth;
        sourceReduceDestGrowth = transactionEntity.getSourceType().equals(EntityTypeEnum.Active.name())
                && transactionEntity.getDestinationType().equals(EntityTypeEnum.ExpenseSource.name());
        sourceReduceDestGrowth |= transactionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())
                && transactionEntity.getDestinationType().equals(EntityTypeEnum.ExpenseSource.name());
        sourceReduceDestGrowth |= transactionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())
                && transactionEntity.getDestinationType().equals(EntityTypeEnum.Active.name())
                && transactionEntity.getBuyingAsset();
        return sourceReduceDestGrowth;
    }

    private Boolean isSourceReduce(TransactionEntity transactionEntity) {
        boolean sourceReduce;
        sourceReduce = transactionEntity.getDestinationType().equals(EntityTypeEnum.ExpenseCategory.name());
        sourceReduce |= transactionEntity.getSourceType().equals(EntityTypeEnum.ExpenseSource.name())
                && transactionEntity.getDestinationType().equals(EntityTypeEnum.Active.name())
                && !transactionEntity.getBuyingAsset();
        return sourceReduce;
    }

    private void updateSourceAndDestinationBalances(TransactionEntity newTransaction,
                                                    Long oldSourceId,
                                                    String oldSourceType,
                                                    Long oldDestinationId,
                                                    String oldDestinationType,
                                                    BigDecimal oldAmount,
                                                    BigDecimal oldConvertedAmount,
                                                    boolean isBuyingAssetsChanged) {
        boolean isAmountsChanged = !newTransaction.getAmountCents().equals(oldAmount)
                || !newTransaction.getConvertedAmountCents().equals(oldConvertedAmount);
        if (isAmountsChanged
                || !newTransaction.getSourceId().equals(oldSourceId)
                || !newTransaction.getSourceType().equals(oldSourceType)) {
            updateSource(newTransaction, oldSourceId, oldSourceType, oldAmount);
        }
        if (isAmountsChanged
                || !newTransaction.getDestinationId().equals(oldDestinationId)
                || !newTransaction.getDestinationType().equals(oldDestinationType)
                || isBuyingAssetsChanged) {
            updateDestination(newTransaction, oldDestinationId, oldDestinationType, oldConvertedAmount, isBuyingAssetsChanged);
        }
    }

    private void updateSource(TransactionEntity newTransaction,
                              Long oldSourceId,
                              String oldSourceType,
                              BigDecimal oldAmount) {
        if (newTransaction.getSourceId().equals(oldSourceId)
                && newTransaction.getSourceType().equals(oldSourceType)) {
            Object source = entityUtil.find(newTransaction.getSourceId(), EntityTypeEnum.valueOf(newTransaction.getSourceType()));
            entityUtil.appendCents(source,
                    oldAmount
                            .add(newTransaction.getAmountCents().negate()));
        } else {
            Object oldSource = entityUtil.find(oldSourceId, EntityTypeEnum.valueOf(oldSourceType));
            Object newSource = entityUtil.find(newTransaction.getSourceId(), EntityTypeEnum.valueOf(newTransaction.getSourceType()));
            entityUtil.appendCents(oldSource, oldAmount);
            entityUtil.removeCents(newSource, newTransaction.getAmountCents());
            setSourceFlags(newTransaction, newSource);
            if (oldSourceType.equals(EntityTypeEnum.Active.name())
                    && !newTransaction.getSourceType().equals(EntityTypeEnum.Active.name())) {
                newTransaction.setProfit(null);
            }
        }
    }

    private void updateDestination(TransactionEntity newTransaction,
                                   Long oldDestinationId,
                                   String oldDestinationType,
                                   BigDecimal oldConvertedAmount,
                                   boolean isBuyingAssetChanged) {
        if (newTransaction.getDestinationId().equals(oldDestinationId)
                && newTransaction.getDestinationType().equals(oldDestinationType)) {
            Object destination = entityUtil.find(newTransaction.getDestinationId(), EntityTypeEnum.valueOf(newTransaction.getDestinationType()));
            if (isBuyingAssetChanged) {
                if (newTransaction.getBuyingAsset()) {
                    entityUtil.appendCents(destination, newTransaction.getConvertedAmountCents());
                } else {
                    entityUtil.removeCents(destination, newTransaction.getConvertedAmountCents());
                }
            } else {
                entityUtil.appendCents(destination,
                        newTransaction.getConvertedAmountCents()
                                .add(oldConvertedAmount.negate()));
            }
        } else {
            Object oldDestination = entityUtil.find(oldDestinationId, EntityTypeEnum.valueOf(oldDestinationType));
            Object newDestination = entityUtil.find(newTransaction.getDestinationId(), EntityTypeEnum.valueOf(newTransaction.getDestinationType()));
            entityUtil.removeCents(oldDestination, oldConvertedAmount);
            if (!newTransaction.getDestinationType().equals(EntityTypeEnum.Active.name())
                    || newTransaction.getDestinationType().equals(EntityTypeEnum.Active.name())
                    && newTransaction.getBuyingAsset())
                entityUtil.appendCents(newDestination, newTransaction.getConvertedAmountCents());
            setDestinationFlags(newTransaction, newDestination);
        }
    }

    private void updateTitlesAndIconUrls(TransactionEntity transactionEntity, HasNameAndIcon source, HasNameAndIcon destination) {
        transactionEntity.setSourceTitle(source.getName());
        transactionEntity.setSourceIconUrl(source.getIconUrl());
        transactionEntity.setDestinationTitle(destination.getName());
        transactionEntity.setDestinationIconUrl(destination.getIconUrl());
    }

    private void setFlags(TransactionEntity transactionEntity, Object source, Object destination) {
        setSourceFlags(transactionEntity, source);
        setDestinationFlags(transactionEntity, destination);
    }

    private void setSourceFlags(TransactionEntity transactionEntity, Object source) {
        transactionEntity.setIsVirtualSource(entityUtil.isVirtual(source));
        transactionEntity.setIsBorrowOrReturnSource(entityUtil.isBorrowOrReturn(source));
        transactionEntity.setIsActiveSource(source instanceof IncomeSourceEntity
                && ((IncomeSourceEntity) source).getActive() != null);
    }

    private void setDestinationFlags(TransactionEntity transactionEntity, Object destination) {
        transactionEntity.setIsVirtualDestination(entityUtil.isVirtual(destination));
        transactionEntity.setIsBorrowOrReturnDestination(entityUtil.isBorrowOrReturn(destination));
    }

    private Timestamp getGotAtFromSaltEdgeTransaction(Transaction transaction) {
        LocalTime time = transaction.getExtra() != null && transaction.getExtra().getTime() != null
                ? transaction.getExtra().getTime()
                : orderedTime(transaction);
        return Timestamp.valueOf(LocalDateTime.of(transaction.getMadeOn(), time));
    }

    private LocalTime orderedTime(Transaction transaction) {
        String id = transaction.getId();
        long offset;
        if(transaction.getId().length() > 9) {
            offset = Long.parseLong(id.substring(9));
        } else {
            offset = Long.parseLong(id);
        }
        return transaction.getCreatedAt().plusNanos(offset).toLocalTime();
    }

    @Data
    @AllArgsConstructor
    public static class ChangeTransactionCF {
        private UserEntity userEntity;
        private Transactionable source;
        private Transactionable destination;
        private BigDecimal amountOfCents;
        private BigDecimal convertedAmountOfCents;
        private boolean buyingAsset;
        private TransactionPurposeEnum transactionPurpose;
        private TransactionNatureEnum transactionNature;
    }

}
