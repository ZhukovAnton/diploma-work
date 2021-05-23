package com.stanum.skrudzh.utils.logic;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.BasketRepository;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUtil {

    private final BasketRepository basketRepository;

    public void checkRightAccess(Long id) {
        if (!id.equals(RequestUtil.getUser().getId())) {
            throw new AppException(HttpAppError.ACCESS_DENIED);
        }
    }

    public void checkRightAccessAcrossBasket(Long id) {
        if (!basketRepository.checkUserId(id, RequestUtil.getUser().getId())) {
            throw new AppException(HttpAppError.ACCESS_DENIED);
        }
    }

    public void checkSubscription(UserEntity userEntity) {
        if (!userEntity.getHasActiveSubscription())
            throw new AppException(HttpAppError.PAYMENT_REQUIRED, "Access not allowed, payment required");
    }

    public BigDecimal getMonthlyMultiplier(PeriodEnum periodEnum) {
        if (periodEnum.equals(PeriodEnum.week)) {
            return new BigDecimal("0.25");
        } else if (periodEnum.equals(PeriodEnum.month)) {
            return BigDecimal.ONE;
        } else if (periodEnum.equals(PeriodEnum.quarter)) {
            return new BigDecimal("3");
        } else {
            return new BigDecimal("12");
        }
    }
}
