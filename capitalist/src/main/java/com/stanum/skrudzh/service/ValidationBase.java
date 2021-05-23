package com.stanum.skrudzh.service;

import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.exception.ValidationException;
import com.stanum.skrudzh.utils.RequestUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class ValidationBase {

    protected static void validateName(Map<String, List<String>> errorMap, String name) {
        if (name == null || name.isBlank()) {
            errorMap.put("name", Collections.singletonList(
                    ResourceBundle
                            .getBundle("messages", RequestUtil.getLocale())
                            .getString("activerecord.errors.models.validation.attributes.not_null")
            ));
        }
    }

    protected static void validateNameWithNull(Map<String, List<String>> errorMap, String name) {
        if (name == null) return;
        if (name.isBlank()) {
            errorMap.put("name", Collections.singletonList(
                    ResourceBundle
                            .getBundle("messages", RequestUtil.getLocale())
                            .getString("activerecord.errors.models.validation.attributes.not_null")
            ));
        }
    }

    protected static void validateMustExists(Map<String, List<String>> errorMap, Long value, String attributeName) {
        if (value == null) {
            errorMap.put(attributeName, Collections.singletonList(
                    ResourceBundle
                            .getBundle("messages", RequestUtil.getLocale())
                            .getString("activerecord.errors.models.validation.attributes.must_exist")
            ));
        }
    }

    //may be add configuration of comparable value
    protected static void validateGreaterThanZero(Map<String, List<String>> errorMap,
                                            Long value,
                                            Boolean isNullAllowed,
                                           String attributeName) {
        if (isNullAllowed) {
            if (value != null && value <= 0) {
                errorMap.put(attributeName, Collections.singletonList(
                        String.format(ResourceBundle
                                .getBundle("messages", RequestUtil.getLocale())
                                .getString("activerecord.errors.models.validation.attributes.amount.greater_than"), "0")
                ));
            }
        }
        else {
            if (value == null || value <= 0) {
                errorMap.put(attributeName, Collections.singletonList(
                        String.format(ResourceBundle
                                .getBundle("messages", RequestUtil.getLocale())
                                .getString("activerecord.errors.models.validation.attributes.amount.greater_than"), "0")
                ));
            }
        }
    }

    protected static void validateGreaterThanOrEqualToZero(Map<String, List<String>> errorMap,
                                                    Long value,
                                                    boolean isNullAllowed,
                                                    String attributeName) {
        if (isNullAllowed) {
            if (value != null && value < 0) {
                errorMap.put(attributeName, Collections.singletonList(
                        String.format(ResourceBundle
                                .getBundle("messages", RequestUtil.getLocale())
                                .getString("activerecord.errors.models.validation.attributes.amount.greater_than_or_equal_to"), "0")
                ));
            }
        }
        else {
            if (value == null || value < 0) {
                errorMap.put(attributeName, Collections.singletonList(
                        String.format(ResourceBundle
                                .getBundle("messages", RequestUtil.getLocale())
                                .getString("activerecord.errors.models.validation.attributes.amount.greater_than_or_equal_to"), "0")
                ));
            }
        }
    }

    protected static void throwErrorIfNeed(Map<String, List<String>> errorMap) {
        if (errorMap.size() > 0) {
            throw new ValidationException(HttpAppError.VALIDATION_FAILED, errorMap);
        }
    }


}
