package com.stanum.skrudzh.aspect;

import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.active.ActiveManagementService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActiveAspect {

    private final ReminderService reminderService;

    private final ActiveManagementService activeManagementService;

    private final OrderService orderService;

    @AfterReturning(value = "execution(* com.stanum.skrudzh.service.active.ActiveManagementService.createActiveEntityWithCreationForm(..)) && args(user, basketEntity,form)",
            returning = "activeEntity")
    public void afterActiveCreation(UserEntity user, BasketEntity basketEntity, ActiveCreationForm.ActiveCF form, ActiveEntity activeEntity) {
        if (form != null) {
            if (form.getReminderAttributes() != null && form.getReminderAttributes().getId() != null) {
                reminderService.saveUpdatedEntity(form.getReminderAttributes(), activeEntity);
            } else {
                reminderService.saveCreatedEntity(form.getReminderAttributes(), activeEntity);
            }
        }

        if(form.getActiveTransactionAttributes() != null && form.getActiveTransactionAttributes().getId() != null) {
            activeManagementService.bindActiveTransaction(activeEntity, form.getActiveTransactionAttributes());
        } else {
            activeManagementService.createAlreadyPaidTransactions(activeEntity);
            activeManagementService.createInitialCostChange(activeEntity, form);
        }
        activeManagementService.createIncomeSource(activeEntity);

        orderService.updateOrder(basketEntity.getUser(),
                OrderType.ACTIVE_BORROW,
                EntityTypeEnum.Active,
                activeEntity.getId(),
                form.getRowOrderPosition());
    }

}