package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.controller.form.BorrowCreationForm;
import com.stanum.skrudzh.controller.form.BorrowUpdatingForm;
import com.stanum.skrudzh.service.ValidationBase;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BorrowValidationService extends ValidationBase {

    public void validateCreationForm(BorrowCreationForm form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanZero(errorMap, form.getAmountCents(), false, "amount");
        throwErrorIfNeed(errorMap);
    }

    public void validateUpdatingForm(BorrowUpdatingForm form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateName(errorMap, form.getName());
        validateGreaterThanZero(errorMap, form.getAmountCents(), true, "amount");
        throwErrorIfNeed(errorMap);
    }
}
