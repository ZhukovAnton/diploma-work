package com.stanum.skrudzh.service.transaction;

import com.google.common.collect.Lists;
import com.stanum.skrudzh.controller.form.TransactionCreationForm;
import com.stanum.skrudzh.controller.form.TransactionUpdatingForm;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.ValidationBase;
import com.stanum.skrudzh.service.saltedge.connectable.ConnectableFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Service
@RequiredArgsConstructor
public class TransactionValidationService extends ValidationBase {

    private final ConnectableFinder connectableFinder;

    public void validateCreationForm(TransactionCreationForm.TransactionCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateGreaterThanZero(errorMap, form.getAmountCents(), false, "amount");
        validateGreaterThanZero(errorMap, form.getConvertedAmountCents(), false, "converted_amount");
        throwErrorIfNeed(errorMap);
    }

    public void validateUpdatingForm(TransactionUpdatingForm.TransactionUF form, TransactionEntity transactionEntity) {
        Map<String, List<String>> errorMap = new HashMap<>();

        Connectable newSource = connectableFinder.find(form.getSourceId(), EntityTypeEnum.valueOf(form.getSourceType()));
        Connectable newDestination = connectableFinder.find(form.getDestinationId(), EntityTypeEnum.valueOf(form.getDestinationType()));
        Connectable oldSource = connectableFinder.find(transactionEntity.getSourceId(), EntityTypeEnum.valueOf(transactionEntity.getSourceType()));
        Connectable oldDestination = connectableFinder.find(transactionEntity.getDestinationId(), EntityTypeEnum.valueOf(transactionEntity.getDestinationType()));
        if (newSource != null && newSource.getAccountConnectionEntity() != null
                && (oldSource == null || !newSource.getId().equals(oldSource.getId()))) {
            errorMap.put("source", Lists.newArrayList(ResourceBundle
                    .getBundle("messages", RequestUtil.getLocale())
                    .getString("activerecord.errors.models.validation.attributes.impossible_to_bind_to_connected_expense_source")));
        }
        if (newDestination != null && newDestination.getAccountConnectionEntity() != null
                && (oldDestination == null || !newDestination.getId().equals(oldDestination.getId()))) {
            errorMap.put("destination", Lists.newArrayList(ResourceBundle
                    .getBundle("messages", RequestUtil.getLocale())
                    .getString("activerecord.errors.models.validation.attributes.impossible_to_bind_to_connected_expense_source")));
        }
        validateGreaterThanZero(errorMap, form.getAmountCents(), false, "amount");
        validateGreaterThanZero(errorMap, form.getConvertedAmountCents(), false, "converted_amount");
        throwErrorIfNeed(errorMap);
    }

}
