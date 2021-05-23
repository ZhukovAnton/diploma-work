package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.controller.form.*;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.SessionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.metrics.Metric;
import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.model.dto.Customer;
import com.stanum.skrudzh.model.dto.Session;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.service.common.CommonService;
import com.stanum.skrudzh.service.onboarding.OnboardingService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.email_confirm.EmailConfirmUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserRequestService {

    private final SessionManagementService sessionManagementService;

    private final UserUtil userUtil;

    private final UserFinder userFinder;

    private final UserDtoService userDtoService;

    private final UserManagementService userManagementService;

    private final UserAuthService userAuthService;

    private final UserPasswordService userPasswordService;

    private final EmailConfirmUtil emailConfirmUtil;

    private final CommonService commonService;

    private final OnboardingService onboardingService;

    private final MetricsService metricsService;

    public Session login(SessionCredentialsForm.SessionForm payload) {
        long start = System.currentTimeMillis();
        UserEntity userEntity = payload.getEmail() == null || payload.getEmail().isBlank()
                ? userManagementService.createGuestUser()
                : userFinder.findUserByEmail(payload.getEmail());

        User userResponse;
        if (!userEntity.getGuest()) {
            if (payload.getPassword() == null
                    || !BCrypt.checkpw(payload.getPassword(), userEntity.getPasswordDigest())) {
                throw new AppException(HttpAppError.UNAUTHORIZED);
            }
        }
        long startDto = System.currentTimeMillis();
        userResponse = userDtoService.createUserDto(userEntity);
        metricsService.saveMetric(MetricType.SAVE_USER_DTO, startDto);

        SessionEntity sessionEntity = sessionManagementService.createSession(userEntity);
        sessionManagementService.save(sessionEntity);

        metricsService.saveMetric(MetricType.LOGIN, start);
        return new Session(sessionEntity.getToken(), userResponse);
    }

    public UserEntity register(UserCreationForm.UserCF userCreationForm, String baseUrl) {
        return userAuthService.register(userCreationForm, baseUrl);
    }

    public UserEntity getUserById(Long id) {
        userUtil.checkRightAccess(id);
        return RequestUtil.getUser();
    }

    public void updateUser(Long id, UserUpdatingForm.UserUF form) {
        userUtil.checkRightAccess(id);
        UserEntity userEntity = RequestUtil.getUser();
        userManagementService.updateUser(userEntity, form);
    }

    public void confirmUser(Long id, String baseUrl) {
        userUtil.checkRightAccess(id);
        UserEntity userEntity = RequestUtil.getUser();
        if (userEntity.getEmailConfirmedAt() != null) throw new AppException(HttpAppError.METHOD_NOT_ALLOWED);
        userAuthService.confirmUser(userEntity, baseUrl);
    }

    public void generateAndSendPassResetCode(PasswordResetCodeCreationForm.SendPasswordResetCodeForm form) {
        userAuthService.generateAndSendPassResetCode(form);
    }

    public void resetPassword(UserPasswordResetForm.PasswordResetForm form) {
        userPasswordService.resetPassword(form);
    }

    public void updatePassword(UserPasswordUpdatingForm.PasswordUpdatingForm form, Long id) {
        userUtil.checkRightAccess(id);
        UserEntity userEntity = RequestUtil.getUser();
        userPasswordService.updatePassword(userEntity, form);
    }

    public Customer getOrCreateSaltEdgeCustomer(Long userId) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        if(userEntity.getSaltEdgeCustomerId() != null && userEntity.getSaltEdgeCustomerSecret() != null) {
            log.info("Customer exist for user {}", userId);
            return new Customer(userEntity.getSaltEdgeCustomerId(), userEntity.getSaltEdgeCustomerSecret());
        }
        return userManagementService.createSaltEdgeCustomer(userEntity);
    }

    public void onboarding(Long id) {
        userUtil.checkRightAccess(id);
        UserEntity userEntity = RequestUtil.getUser();
        onboardingService.onboarding(userEntity);
    }

    public void destroyUsersData(Long id) {
        userUtil.checkRightAccess(id);
        UserEntity userEntity = RequestUtil.getUser();
        commonService.destroyUsersData(userEntity);
        userEntity.setOnBoarded(false);
        userManagementService.save(userEntity);
    }

    public ModelAndView confirmEmailByCode(String code) {
        ModelAndView modelAndView = new ModelAndView();
        UserEntity user = userFinder.findByEmailConfirmationCode(code).orElse(null);

        if (user == null) {
            return emailConfirmUtil.createPageForExpiredLink("confirm-link-expired", modelAndView);
        } else {
            if (user.getEmailConfirmedAt() != null) {
                return emailConfirmUtil.createPageForConfirmedUser(user, "registration-confirmed", modelAndView);
            } else {
                if (userAuthService.confirmEmail(code)) {
                    return emailConfirmUtil.createPageForConfirmedUser(user, "registration-confirmed", modelAndView);
                } else {
                    ModelAndView model = emailConfirmUtil
                            .createPageForFailedConfirm(user, "registration-confirmation-failed", modelAndView);
                    model.setStatus(HttpStatus.FOUND);
                    return model;
                }
            }
        }
    }

}
