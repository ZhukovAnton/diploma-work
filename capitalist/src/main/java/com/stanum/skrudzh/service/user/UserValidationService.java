package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.controller.form.UserCreationForm;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.service.ValidationBase;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserValidationService extends ValidationBase {

    private final UserRepository userRepository;

    private final Encryptor encryptor;

    public void validateCreationForm(UserCreationForm.UserCF form) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validateEmail(errorMap, form.getEmail());
        validatePasswords(errorMap, form.getPassword(), form.getPasswordConfirmation());
        throwErrorIfNeed(errorMap);
    }

    public void validatePasswords(String password, String passwordConfirmation) {
        Map<String, List<String>> errorMap = new HashMap<>();
        validatePasswords(errorMap, password, passwordConfirmation);
        throwErrorIfNeed(errorMap);
    }

    private void validatePasswords(Map<String, List<String>> errorMap, String password, String passwordConfirmation) {
        if (password == null || password.isBlank()) {
            errorMap.put("password", Collections.singletonList(ResourceBundle
                    .getBundle("messages", RequestUtil.getLocale())
                    .getString("activerecord.errors.models.validation.attributes.not_null")));
        }
        if (password != null && !password.isBlank() && password.length() < 6) {
            errorMap.put("password",
                    Collections.singletonList(String.format(ResourceBundle
                            .getBundle("messages", RequestUtil.getLocale())
                            .getString("activerecord.errors.models.user.attributes.password.too_short"), 6)
                    ));
        }
        if (password != null && !password.isBlank() && password.length() > 20) {
            errorMap.put("password",
                    Collections.singletonList(String.format(ResourceBundle
                            .getBundle("messages", RequestUtil.getLocale())
                            .getString("activerecord.errors.models.user.attributes.password.too_long"), 20)
                    ));
        }
        if (passwordConfirmation == null || passwordConfirmation.isBlank()) {
            errorMap.put("password_confirmation", Collections.singletonList(ResourceBundle
                    .getBundle("messages", RequestUtil.getLocale())
                    .getString("activerecord.errors.models.validation.attributes.not_null")));
        }
        else {
            if ((password != null && !password.isBlank() && !password.equals(passwordConfirmation))) {
                errorMap.put("password_confirmation", Collections.singletonList(
                        ResourceBundle
                                .getBundle("messages", RequestUtil.getLocale())
                                .getString("activerecord.errors.models.user.attributes.password.not_matches")));
            }
        }
    }

    private void validateEmail(Map<String, List<String>> errorMap, String email) {
        if (email == null || email.isBlank()) {
            errorMap.put("email", Collections.singletonList(ResourceBundle
                    .getBundle("messages", RequestUtil.getLocale())
                    .getString("activerecord.errors.models.validation.attributes.not_null")));
        } else {
            if (userRepository.existsByEmailIgnoreCase(encryptor.convertToDatabaseColumn(email.toLowerCase()))) {
                errorMap.put("email", Collections.singletonList(ResourceBundle
                        .getBundle("messages", RequestUtil.getLocale())
                        .getString("activerecord.errors.models.user.attributes.email.taken")));
            }
        }
    }

}
