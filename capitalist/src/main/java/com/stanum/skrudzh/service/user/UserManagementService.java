package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.controller.form.UserUpdatingForm;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.metrics.Metric;
import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.saltage.model.Customer;
import com.stanum.skrudzh.saltage.model.Response;
import com.stanum.skrudzh.service.basket.BasketService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;

    private final BasketService basketService;

    private final SaltedgeAPI saltedgeAPI;

    private final MetricsService metricsService;

    @Value("${threebaskets.env}")
    private String environment;

    public UserEntity createGuestUser() {
        long start = System.currentTimeMillis();
        log.info("Create guest user");
        UserEntity guest = new UserEntity();
        Timestamp now = TimeUtil.now();
        guest.setGuest(true);
        guest.setEmail("guest_" + new Date().getTime() + (long) (Math.random() % 99) + "@scrooge.bz");
        guest.setCreatedAt(now);
        guest.setUpdatedAt(now);
        guest.setHasActiveSubscription(false);
        guest.setDefaultCurrency(CurrencyService.rubIsoCode);
        guest.setDefaultPeriod(PeriodEnum.month);
        guest.setLocale(RequestUtil.getLocale().toString());
        guest.setOnBoarded(false);
        save(guest);
        basketService.createBasketByType(BasketTypeEnum.joy, guest);
        basketService.createBasketByType(BasketTypeEnum.safe, guest);
        basketService.createBasketByType(BasketTypeEnum.risk, guest);
        metricsService.saveMetric(MetricType.CREATE_GUEST_USER, start);
        return guest;
    }

    public void updateUser(UserEntity userEntity, UserUpdatingForm.UserUF form) {
        checkAndUpdateWithUpdatingForm(userEntity, form);
        userEntity.setUpdatedAt(TimeUtil.now());
        save(userEntity);
    }

    public void updateLocale(UserEntity userEntity, String locale) {
        userEntity.setLocale(locale);
        save(userEntity);
    }

    public UserEntity save(UserEntity userEntity) {
        long start = System.currentTimeMillis();
        UserEntity savedUser = userRepository.save(userEntity);
        metricsService.saveMetric(MetricType.SAVE_USER_TO_DB, start);
        return savedUser;
    }

    private void checkAndUpdateWithUpdatingForm(UserEntity userEntity, UserUpdatingForm.UserUF form) {
        if (form.getFirstname() != null) userEntity.setFirstname(form.getFirstname());
        if (form.getDeviceToken() != null) userEntity.setDeviceToken(form.getDeviceToken());
        if (form.getSaltEdgeCustomerSecret() != null)
            userEntity.setSaltEdgeCustomerSecret(form.getSaltEdgeCustomerSecret());
        if (form.getDefaultPeriod() != null)
            userEntity.setDefaultPeriod(PeriodEnum.valueOf(form.getDefaultPeriod()));
        if (form.getDefaultCurrency() != null) userEntity.setDefaultCurrency(form.getDefaultCurrency());
        if (form.getHasActiveSubscription() != null)
            userEntity.setHasActiveSubscription(form.getHasActiveSubscription());
        if (form.getPlannedIncomeSavingAttributes() != null
                && form.getPlannedIncomeSavingAttributes().getPercentCents() != null)
            userEntity.setPlannedSavingPercent(BigDecimal
                    .valueOf(form.getPlannedIncomeSavingAttributes().getPercentCents()));
    }

    public com.stanum.skrudzh.model.dto.Customer createSaltEdgeCustomer(UserEntity userEntity) {
        if (userEntity.getSaltEdgeCustomerId() != null) {
            log.info("SaltEdge customer already exists for user {}", userEntity.getId());
            return new com.stanum.skrudzh.model.dto.Customer(userEntity.getSaltEdgeCustomerId(), userEntity.getSaltEdgeCustomerSecret());
        }
        log.info("Try to create SaltEdge customer for user {}", userEntity.getId());
        long start = System.currentTimeMillis();
        List<Customer> serviceCustomers = saltedgeAPI.custom.findAllCustomers();
        String saltEdgeUserIdentifier = saltEdgeUserIdentifier(userEntity);

        Optional<Customer> existingCustomerOptional = serviceCustomers
                .stream()
                .filter(customer -> customer.getIdentifier().equals(saltEdgeUserIdentifier))
                .findFirst();

        String customerId;
        String secret;
        if (existingCustomerOptional.isPresent()) {
            log.info("Customer was found for saltEdgeUserIdentifier {}, user {}", saltEdgeUserIdentifier, userEntity.getId());
            customerId = existingCustomerOptional.get().getCustomerId();
            secret = existingCustomerOptional.get().getSecret();
        } else {
            log.info("Create new SaltEdge customer for user {}", userEntity.getId());
            Response<Customer> customerResponse = saltedgeAPI.customer.create(saltEdgeUserIdentifier);
            if (customerResponse.hasError() || customerResponse.getData().isEmpty()) {
                log.error("Error customer response: {}", customerResponse);
                throw new RuntimeException("Error while creating SaltEdge customer");
            }
            customerId = customerResponse.getData().get(0).getCustomerId();
            secret = customerResponse.getData().get(0).getSecret();
        }
        userEntity.setSaltEdgeCustomerId(customerId);
        userEntity.setSaltEdgeCustomerSecret(secret);
        log.info("Get customer took {}", System.currentTimeMillis() - start);

        save(userEntity);
        metricsService.saveMetric(MetricType.CREATE_SALTEDGE_CUSTOMER, start);
        return new com.stanum.skrudzh.model.dto.Customer(customerId, secret);
    }

    private String saltEdgeUserIdentifier(UserEntity userEntity) {
        return environment + "_user_#" + userEntity.getId();
    }

}
