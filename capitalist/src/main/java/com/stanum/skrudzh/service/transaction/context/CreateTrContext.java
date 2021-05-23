package com.stanum.skrudzh.service.transaction.context;

import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.model.base.Transactionable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTrContext {
    private Transactionable source;
    private Transactionable destination;
    private UserEntity userEntity;
    private TransactionCreationForm.TransactionCF form;
}
