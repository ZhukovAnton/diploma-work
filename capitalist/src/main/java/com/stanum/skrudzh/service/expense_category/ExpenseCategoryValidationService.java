package com.stanum.skrudzh.service.expense_category;

import com.stanum.skrudzh.controller.form.ExpenseCategoryCreationForm;
import com.stanum.skrudzh.controller.form.ExpenseCategoryUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseCategoryValidationService extends ValidationBase {

    public void validateCreationForm(ExpenseCategoryCreationForm.ExpenseCategoryCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (form.getPrototypeKey() == null) {
            validateName(errorMap, form.getName());
        }
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPlannedCents(), true, "monthly_planned");
        throwErrorIfNeed(errorMap);
    }

    public void validateUpdatingForm(ExpenseCategoryUpdatingForm.ExpenseCategoryUF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPlannedCents(), true, "monthly_planned");
        throwErrorIfNeed(errorMap);
    }
}
