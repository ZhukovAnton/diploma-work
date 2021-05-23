package com.stanum.skrudzh.aspect;

import com.stanum.skrudzh.controller.form.BorrowCreationForm;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.borrow.BorrowTransactionService;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Aspect
@Component
@RequiredArgsConstructor
public class BorrowAspect {

    private final ReminderService reminderService;

    private final BorrowTransactionService transactionService;

    private final OrderService orderService;

    @AfterReturning(value = "execution(* com.stanum.skrudzh.service.borrow.BorrowManagementService.createBorrowWithCreationForm(..)) && args(userEntity, form, borrowType)",
            returning = "borrowEntity")
    public void afterBorrowCreation(BorrowEntity borrowEntity, UserEntity userEntity, BorrowCreationForm form, BorrowTypeEnum borrowType) {
        Reminder reminder = null;
        if (borrowEntity.getPayday() != null) {
            reminder = new Reminder();
            reminder.setStartDate(borrowEntity.getPayday()
                    .toLocalDateTime()
                    .atOffset(ZoneOffset.UTC)
                    .withSecond(0).withNano(0));
        }
        if (reminder != null && reminder.getId() != null) {
            reminderService.saveUpdatedEntity(reminder, borrowEntity);
        } else {
            reminderService.saveCreatedEntity(reminder, borrowEntity);
        }

        if(RequestUtil.hasGlobalSorting()) {
            orderService.updateOrder(borrowEntity.getUser(),
                    borrowType == BorrowTypeEnum.Debt ? OrderType.ACTIVE_BORROW : OrderType.CREDIT_BORROW,
                    EntityTypeEnum.Borrow,
                    borrowEntity.getId(),
                    form.getRowOrderPosition());
        }
    }

    @Before(value = "execution(* com.stanum.skrudzh.service.borrow.BorrowManagementService.destroyBorrow(..)) && args(borrowEntity, ..)")
    public void beforeBorrowDestruction(BorrowEntity borrowEntity) {
        TransactionEntity borrowingTransaction = transactionService.getBorrowingTransaction(borrowEntity);
        if (borrowingTransaction == null) return;
        borrowingTransaction.setDeletedAt(TimeUtil.now());
        transactionService.afterDestroy(borrowingTransaction);
        transactionService.save(borrowingTransaction);
    }

}