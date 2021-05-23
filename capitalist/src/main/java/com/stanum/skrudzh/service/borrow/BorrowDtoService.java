package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.dto.Borrow;
import com.stanum.skrudzh.model.dto.Debts;
import com.stanum.skrudzh.model.dto.Loans;
import com.stanum.skrudzh.model.dto.Reminder;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowDtoService {

    private final BorrowTransactionService transactionService;

    private final BorrowCalculationService calculationService;

    private final CurrencyService currencyService;

    private final ReminderService reminderService;

    public Debts createDebtsResponse(Set<BorrowEntity> borrowEntities) {
        return new Debts(borrowEntities.stream().map(this::createBorrowResponse).collect(Collectors.toList()));
    }

    public Loans createLoansResponse(Set<BorrowEntity> borrowEntities) {
        return new Loans(borrowEntities.stream().map(this::createBorrowResponse).collect(Collectors.toList()));
    }

    public Borrow createBorrowResponse(BorrowEntity borrowEntity) {
        BigDecimal returnedAmount = calculationService.returnedAmount(borrowEntity);
        TransactionEntity borrowingTransaction = transactionService.getBorrowingTransaction(borrowEntity);
        Borrow borrow = new Borrow(borrowEntity);
        borrow.setCurrency(currencyService.getCurrencyByIsoCode(borrowEntity.getAmountCurrency()));
        borrow.setBorrowingTransactionId(borrowingTransaction != null ? borrowingTransaction.getId() : null);
        borrow.setReturnedAmountCents(returnedAmount.longValue());
        borrow.setAmountCentsLeft(borrowEntity.getAmountCents().add(returnedAmount.negate()).longValue());
        ReminderEntity reminderEntity = reminderService.findBySourceIdAndType(borrowEntity.getId(), RemindableTypeEnum.Borrow);
        Reminder reminder = reminderEntity != null ? new Reminder(reminderEntity) : null;
        borrow.setReminder(reminder);
        return borrow;
    }

}
