package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.controller.form.PasswordResetCodeCreationForm;
import com.stanum.skrudzh.controller.form.UserCreationForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.utils.CodeGenerator;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAuthService {

    private final UserValidationService validationService;

    private final UserManagementService userManagementService;

    private final UserFinder userFinder;

    private final EmailService emailService;

    public UserEntity register(UserCreationForm.UserCF userCreationForm, String baseUrl) {
        validationService.validateCreationForm(userCreationForm);
        UserEntity userEntity = RequestUtil.getUser();
        fillUserEntityWithCreationForm(userEntity, userCreationForm);
        confirmUser(userEntity, baseUrl);
        userManagementService.save(userEntity);
        return userEntity;
    }

    public Boolean confirmEmail(String code) {
        if (code == null) {
            throw new AppException(HttpAppError.BAD_REQUEST);
        }
        Optional<UserEntity> userEntityOptional = userFinder.findByEmailConfirmationCode(code);
        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            user.setEmailConfirmedAt(TimeUtil.now());
            userManagementService.save(user);
        }
        return userEntityOptional.isPresent();
    }

    public void confirmUser(UserEntity userEntity, String baseUrl) {
        userEntity.setEmailConfirmationCode(CodeGenerator.generateCode());
        userManagementService.save(userEntity);
        emailService.sendConfirmationEmail(userEntity, baseUrl);
    }

    public void generateAndSendPassResetCode(PasswordResetCodeCreationForm.SendPasswordResetCodeForm form) {
        UserEntity userEntity;
        try {
            userEntity = userFinder.findUserByEmail(form.getEmail().toLowerCase());
        } catch (Exception e) {
            log.error("Error while searching for a user with form = {}", form);
            throw new AppException(HttpAppError.NOT_FOUND);
        }
        userEntity.setPasswordResetCode(CodeGenerator.generateCode(8));
        userEntity.setPasswordResetAttempts(0);
        userEntity.setPasswordResetCodeCreatedAt(TimeUtil.now());

        userManagementService.save(userEntity);
        emailService.sendPasswordResetCodeEmail(userEntity);
    }

    private void fillUserEntityWithCreationForm(UserEntity userEntity, UserCreationForm.UserCF userCreationForm) {
        Timestamp now = TimeUtil.now();
        userEntity.setUpdatedAt(now);
        userEntity.setFirstname(userCreationForm.getFirstname());
        userEntity.setLastname(userCreationForm.getLastname());
        userEntity.setEmail(userCreationForm.getEmail().toLowerCase());
        userEntity.setPasswordDigest(BCrypt.hashpw(userCreationForm.getPassword(), BCrypt.gensalt()));
        userEntity.setGuest(false);
        userEntity.setLocale(RequestUtil.getLocale().toString());
    }



}
