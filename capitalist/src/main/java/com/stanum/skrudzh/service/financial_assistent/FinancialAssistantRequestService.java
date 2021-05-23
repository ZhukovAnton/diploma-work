package com.stanum.skrudzh.service.financial_assistent;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinancialAssistantRequestService {

    private final UserUtil userUtil;

    private final FinancialAssistantCalculationService calculationService;

    public long getFreeMoney(Long userId, PeriodEnum periodEnum) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return calculationService.calculateFreeUsersMoneyPerPeriod(userEntity, periodEnum);
    }

    public void plannedIncomeSaving(Long userId) {
        userUtil.checkRightAccess(userId);
    }
}
