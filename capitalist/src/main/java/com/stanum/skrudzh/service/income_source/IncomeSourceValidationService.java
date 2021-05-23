package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeSourceValidationService extends ValidationBase {

    public static void validateCreationForm(IncomeSourceCreationForm.IncomeSourceCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (form.getPrototypeKey() == null) {
            validateName(errorMap, form.getName());
        }
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPlannedCents(), true, "monthly_planned");
        throwErrorIfNeed(errorMap);
    }

    public static void validateUpdatingForm(IncomeSourceUpdatingForm.IncomeSourceUF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPlannedCents(), true, "monthly_planned");
        throwErrorIfNeed(errorMap);
    }
}
