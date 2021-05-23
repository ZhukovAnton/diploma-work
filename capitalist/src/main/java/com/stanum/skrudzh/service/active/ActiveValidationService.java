package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.form.ActiveUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActiveValidationService extends ValidationBase {

    public void validateCreationForm(ActiveCreationForm.ActiveCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateMustExists(errorMap, form.getActiveTypeId(), "active_type");
        validateGreaterThanOrEqualToZero(errorMap, form.getAlreadyPaidCents(), true, "already_paid");
        validateGreaterThanZero(errorMap, form.getGoalAmountCents(), true, "goal_amount");
        validateGreaterThanZero(errorMap, form.getMonthlyPaymentCents(), true, "monthly_payment");
        validateGreaterThanZero(errorMap, form.getMonthlyPlannedIncomeCents(), true, "monthly_planned_income");
        throwErrorIfNeed(errorMap);
    }

    public void validateUpdatingForm(ActiveUpdatingForm.ActiveUF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanZero(errorMap, form.getGoalAmountCents(), true, "goal_amount");
        validateGreaterThanZero(errorMap, form.getMonthlyPaymentCents(), true, "monthly_payment");
        validateGreaterThanZero(errorMap, form.getMonthlyPlannedIncomeCents(), true, "monthly_planned_income");
        throwErrorIfNeed(errorMap);
    }

}
