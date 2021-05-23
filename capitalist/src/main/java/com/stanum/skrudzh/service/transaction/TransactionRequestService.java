package com.stanum.skrudzh.service.transaction;

import com.stanum.skrudzh.config.Limits;
import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.saltedge.learn.LearnSaltedgeService;
import com.stanum.skrudzh.service.transaction.context.CreateTrContext;
import com.stanum.skrudzh.service.transaction.context.UpdateTrContext;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionRequestService {

    private final UserUtil userUtil;

    private final TransactionManagementService transactionManagementService;

    private final TransactionCalculationService calculationService;

    private final TransactionFinder finder;

    private final Limits limits;

    private final LearnSaltedgeService learnSaltedgeService;

    private final EntityUtil entityUtil;

    public TransactionEntity createTransactionWithForm(Long userId, TransactionCreationForm.TransactionCF form) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        checkSubscriptions(userEntity);

        CreateTrContext context = CreateTrContext.builder()
                .source(entityUtil.find(form.getSourceId(), EntityTypeEnum.valueOf(form.getSourceType())))
                .destination(entityUtil.find(form.getDestinationId(), EntityTypeEnum.valueOf(form.getDestinationType())))
                .userEntity(userEntity)
                .form(form)
                .build();

        return transactionManagementService.createTransaction(context);
    }

    public TransactionEntity getTransactionById(Long id) {
        TransactionEntity transactionEntity = finder
                .findByIdWithDeleted(id);
        userUtil.checkRightAccess(transactionEntity.getUser().getId());
        return transactionEntity;
    }

    public List<TransactionEntity> getTransactionsByUserAndParams(Long userId,
                                                                  String transactionType,
                                                                  Long transactionableId,
                                                                  String transactionableType,
                                                                  Long creditId,
                                                                  Long borrowId,
                                                                  String borrowType,
                                                                  String lastGotAt,
                                                                  String fromGotAt,
                                                                  String toGotAt,
                                                                  Integer count) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        Timestamp from = fromGotAt != null
                ? RequestUtil.transformIntoUsersTime(Timestamp.valueOf(TimeUtil.parseParamTimestamp(fromGotAt)))
                : TimeUtil.beginningOfTheTime();
        Timestamp till = toGotAt != null
                ? RequestUtil.transformIntoUsersTime(Timestamp.valueOf(TimeUtil.parseParamTimestamp(toGotAt)))
                : lastGotAt != null
                ? Timestamp.valueOf(TimeUtil.parseParamTimestamp(lastGotAt).minusSeconds(1))
                : TimeUtil.now();
        if (!userEntity.getHasActiveSubscription() && fromGotAt == null) {
            from = RequestUtil.transformIntoUsersTime(RequestUtil.transformIntoUsersTime(TimeUtil.beginningOfPreviousMonth()));
        }
        Set<TransactionEntity> transactionEntities;
        if (transactionableId != null && transactionableType != null) {
            transactionEntities = finder.findAllByIdTypeAndParams(transactionableId, transactionableType,
                    transactionType, from, till);
        } else if (creditId != null) {
            transactionEntities = finder.findAllByCreditIdInPeriod(creditId, from, till);
        } else if (borrowId != null && borrowType != null) {
            transactionEntities = finder.findAllByBorrowIdTypeInPeriod(borrowId, borrowType, from, till);
        } else {
            transactionEntities = finder.findAllByUserInPeriod(userEntity, from, till);
        }
        return count == null
                ? transactionEntities.stream()
                .sorted(Comparator.comparing(TransactionEntity::getGotAt).reversed())
                .collect(Collectors.toList())
                : transactionEntities.stream()
                .sorted(Comparator.comparing(TransactionEntity::getGotAt).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void updateTransactionById(Long id, TransactionUpdatingForm.TransactionUF form) {
        TransactionEntity target = getTransactionById(id);
        TransactionEntity source = SerializationUtils.clone(target);

        UpdateTrContext context = UpdateTrContext.builder()
                .form(form)
                .source(source)
                .target(target)
                .build();
        transactionManagementService.updateTransaction(context);

        if(form.getUpdateSimilarTransactions() != null && form.getUpdateSimilarTransactions()) {
            transactionManagementService.updateSimilarTransaction(source, form);
            learnSaltedgeService.learn(target);
        }
    }

    public void destroyTransactionById(Long id) {
        TransactionEntity transactionForDestroying = getTransactionById(id);
        transactionManagementService.destroyTransaction(transactionForDestroying);
    }

    public void duplicateTransactionById(Long id) {
        TransactionEntity transactionForDuplicate = getTransactionById(id);
        transactionManagementService.duplicateTransaction(transactionForDuplicate);
    }

    private void checkSubscriptions(UserEntity userEntity) {
        if (userEntity.getHasActiveSubscription()) return;
        Timestamp from = TimeUtil.beginningOfDay();
        Timestamp till = TimeUtil.endOfDay();
        long amountOfTransactions = calculationService.countTransactionsInPeriod(userEntity, from, till);
        if (amountOfTransactions >= limits.getTransactionLimit()) {
            log.info("User with id={} exceeds transactions limit");
            throw new AppException(HttpAppError.PAYMENT_REQUIRED, "Access not allowed, payment required");
        }
    }


}
