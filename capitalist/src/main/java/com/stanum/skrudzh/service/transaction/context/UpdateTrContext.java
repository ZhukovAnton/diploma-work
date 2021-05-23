package com.stanum.skrudzh.service.transaction.context;

import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTrContext {
    private TransactionEntity source;
    private TransactionEntity target;
    private TransactionUpdatingForm.TransactionUF form;
    private boolean updateSimilar;

    boolean isBuyingAssetsChanged;
    boolean isAmountChanged;
}
