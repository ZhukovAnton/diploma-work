package com.stanum.skrudzh.service.transaction;

import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.service.transaction.context.CreateTrContext;
import com.stanum.skrudzh.service.transaction.context.UpdateTrContext;

import java.util.Optional;

public interface TransactionManagementService {

    TransactionEntity createTransaction(CreateTrContext context);

    void updateSimilarTransaction(TransactionEntity transactionEntity, TransactionUpdatingForm.TransactionUF form);

    void updateTransaction(UpdateTrContext context);

    void destroyTransaction(TransactionEntity transactionForDestroying);

    void duplicateTransaction(TransactionEntity transactionForDuplicate);

    Optional<Hashable> getHashableFromTransaction(TransactionEntity transactionEntity);
}
