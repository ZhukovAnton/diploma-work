package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.controller.form.UserPasswordResetForm;
import com.stanum.skrudzh.controller.form.UserPasswordUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final UserManagementService userManagementService;

    private final UserValidationService validationService;

    private final UserFinder userFinder;

    public void resetPassword(UserPasswordResetForm.PasswordResetForm form) {
        UserEntity userEntity;
        //try-catch for error coincidence
        try {
            userEntity = userFinder.findUserByEmail(form.getEmail());
        } catch (Exception e) {
            throw new AppException(HttpAppError.ACCESS_DENIED);
        }
        if (!checkPasswordResetCode(userEntity, form.getPasswordResetCode())) {
            userEntity.setPasswordResetAttempts(userEntity.getPasswordResetAttempts() + 1);
            userManagementService.save(userEntity);
            throw new AppException(HttpAppError.ACCESS_DENIED);
        }
        removePasswordResetCode(userEntity);
        updatePassword(userEntity, form.getPassword(), form.getPasswordConfirmation());
        userEntity.setPasswordResetAttempts(userEntity.getPasswordResetAttempts() + 1);
        userEntity.setUpdatedAt(TimeUtil.now());
        userManagementService.save(userEntity);
    }

    public void updatePassword(UserEntity userEntity, UserPasswordUpdatingForm.PasswordUpdatingForm form) {
        if (form.getOldPassword() != null && !form.getOldPassword().isBlank()
                && BCrypt.checkpw(form.getOldPassword(), userEntity.getPasswordDigest())) {
            updatePassword(userEntity, form.getNewPassword(), form.getNewPasswordConfirmation());
            userEntity.setUpdatedAt(TimeUtil.now());
            userManagementService.save(userEntity);
        } else {
            throw new AppException(HttpAppError.ACCESS_DENIED);
        }
    }

    private Boolean checkPasswordResetCode(UserEntity userEntity, String passwordResetCode) {
        return userEntity.getPasswordResetCode().equals(passwordResetCode)
                && userEntity.getPasswordResetCodeCreatedAt() != null
                && new Timestamp(TimeUtil.now().getTime() - Constants.TEN_MINUTES_MILLIS)
                .before(userEntity.getPasswordResetCodeCreatedAt())
                && userEntity.getPasswordResetAttempts() < 2;

    }

    private void removePasswordResetCode(UserEntity userEntity) {
        userEntity.setPasswordResetCode(null);
        userEntity.setPasswordResetCodeCreatedAt(null);
        userEntity.setPasswordResetAttempts(0);
    }

    private void updatePassword(UserEntity userEntity, String password, String passwordConfirmation) {
        validationService.validatePasswords(password, passwordConfirmation);
        userEntity.setPasswordDigest(BCrypt.hashpw(password, BCrypt.gensalt()));
    }

}
