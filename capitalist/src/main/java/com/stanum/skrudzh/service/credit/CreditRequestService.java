package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.controller.form.CreditCreationForm;
import com.stanum.skrudzh.controller.form.CreditUpdatingForm;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreditRequestService {

    private final CreditFinder finder;

    private final CreditManagementService managementService;

    private final UserUtil userUtil;

    private final OrderService orderService;

    public CreditEntity createCreditWithCreationForm(Long userId, CreditCreationForm.CreditCF form) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        userUtil.checkSubscription(userEntity);
        return managementService.createCreditWithCreationForm(userEntity, form);
    }

    public Set<CreditEntity> indexCreditsByUserId(Long userId) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return finder.findUnpaidCredits(userEntity);
    }

    public CreditEntity getCreditById(Long id) {
        CreditEntity creditEntity = finder.findById(id);
        managementService.updateIsPaid(creditEntity);
        userUtil.checkRightAccess(creditEntity.getUser().getId());
        return creditEntity;
    }

    public void updateCredit(Long id, CreditUpdatingForm.CreditUF form) {
        CreditEntity creditEntity = getCreditById(id);
        if (form.getRowOrderPosition() != null && RequestUtil.hasGlobalSorting()) {
            orderService.updateOrder(RequestUtil.getUser(),
                    OrderType.CREDIT_BORROW,
                    EntityTypeEnum.Credit,
                    creditEntity.getId(),
                    form.getRowOrderPosition()
            );
            return;
        }
        managementService.updateCredit(creditEntity, form);
    }

    public void destroyCredit(Long id) {
        CreditEntity creditEntity = getCreditById(id);
        managementService.destroyCredit(creditEntity, false);
    }

}
