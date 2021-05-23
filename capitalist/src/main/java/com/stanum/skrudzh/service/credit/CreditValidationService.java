package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.form.CreditUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreditValidationService extends ValidationBase {

    public void validateCreationForm(CreditCreationForm.CreditCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanZero(errorMap, form.getAmountCents(), false, "amount");
        validateGreaterThanZero(errorMap, form.getReturnAmountCents(), false, "return_amount");
        validateGreaterThanOrEqualToZero(errorMap, form.getAlreadyPaidCents(), true, "already_paid");
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPaymentCents(), true, "monthly_payment");
        throwErrorIfNeed(errorMap);
    }

    public void validateUpdatingForm(CreditUpdatingForm.CreditUF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanZero(errorMap, form.getAmountCents(), true, "amount");
        validateGreaterThanZero(errorMap, form.getReturnAmountCents(), true, "return_amount");
        validateGreaterThanOrEqualToZero(errorMap, form.getMonthlyPaymentCents(), true, "monthly_payment");
        throwErrorIfNeed(errorMap);
    }

}
