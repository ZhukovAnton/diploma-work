package com.stanum.skrudzh.service.expense_source;

import com.stanum.skrudzh.controller.form.ExpenseSourceCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseSourceUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseSourceValidationService extends ValidationBase {

    public static void validateCreationForm(ExpenseSourceCreationForm.ExpenseSourceCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanOrEqualToZero(errorMap, form.getCreditLimitCents(), true, "credit_limit");
        throwErrorIfNeed(errorMap);
    }

    public static void validateUpdatingForm(ExpenseSourceUpdatingForm.ExpenseSourceUF form, boolean isNullAllowed) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (isNullAllowed) {
            validateNameWithNull(errorMap, form.getName());
        } else {
            validateName(errorMap, form.getName());
        }
        validateGreaterThanOrEqualToZero(errorMap, form.getCreditLimitCents(), true, "credit_limit");
        throwErrorIfNeed(errorMap);
    }

}
