package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.controller.form.BorrowCreationForm;
import com.stanum.skrudzh.controller.form.BorrowUpdatingForm;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.ReminderEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.BorrowRepository;
import com.stanum.skrudzh.model.dto.Debts;
import com.stanum.skrudzh.model.dto.Loans;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.RemindableTypeEnum;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.reminder.ReminderService;
import com.stanum.skrudzh.service.transaction.TransactionFinder;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowManagementService {

    private final CurrencyService currencyService;

    private final BorrowRepository borrowRepository;

    private final BorrowTransactionService transactionService;

    private final ReminderService reminderService;

    private final BorrowValidationService validationService;

    private final BorrowDtoService borrowDtoService;

    private final BorrowCalculationService calculationService;

    private final TransactionFinder transactionFinder;

    public BorrowEntity createBorrowWithCreationForm(UserEntity userEntity, BorrowCreationForm form, BorrowTypeEnum borrowType) {
        BorrowEntity borrowEntity = new BorrowEntity();
        borrowEntity.setUser(userEntity);
        borrowEntity.setType(borrowType);
        checkAndFillBorrowWithCreationForm(borrowEntity, form);
        save(borrowEntity);
        if(form.getBorrowingTransactionAttributes() != null && form.getBorrowingTransactionAttributes().getId() != null) {
            transactionService.bindBorrowWithTransaction(borrowEntity,
                    transactionFinder.findByIdWithDeleted(form.getBorrowingTransactionAttributes().getId()));
        } else {
            transactionService.createBorrowingTransaction(borrowEntity, form.getBorrowingTransactionAttributes());
        }        return borrowEntity;
    }

    public void updateBorrow(BorrowEntity borrowEntity, BorrowUpdatingForm form) {
        checkAndUpdateBorrowWithForm(borrowEntity, form);
        save(borrowEntity);
    }

    public void destroyBorrow(BorrowEntity borrowEntity, boolean isNeedToDestroyTransactions) {
        if (isNeedToDestroyTransactions) {
            destroyTransactions(borrowEntity);
        }
        borrowEntity.setDeletedAt(TimeUtil.now());
        save(borrowEntity);
    }

    public Debts getWaitingDebts(UserEntity userEntity, String currencyCode) {
        Set<BorrowEntity> waitingDebts = borrowRepository
                .getNotReturnedBorrowsByCurrency(userEntity, BorrowTypeEnum.Debt, currencyCode);
        return borrowDtoService.createDebtsResponse(waitingDebts);
    }

    public Loans getWaitingLoans(UserEntity userEntity, String currencyCode) {
        Set<BorrowEntity> waitingLoans = borrowRepository
                .getNotReturnedBorrowsByCurrency(userEntity, BorrowTypeEnum.Loan, currencyCode);
        return borrowDtoService.createLoansResponse(waitingLoans);
    }

    public void save(BorrowEntity borrowEntity) {
        borrowRepository.save(borrowEntity);
    }

    public void updateIsReturnedWithTransaction(BorrowEntity borrowEntity, TransactionEntity borrowingTransaction) {
        BigDecimal alreadyReturnedAmount = calculationService.returnedAmount(borrowEntity);
        borrowEntity.setIsReturned(alreadyReturnedAmount.compareTo(borrowEntity.getAmountCents()) >= 0);
        borrowingTransaction.setIsReturned(borrowEntity.getIsReturned());
        transactionService.save(borrowingTransaction);
        save(borrowEntity);
    }

    public String notificationLocKey(BorrowEntity borrowEntity) {
        if (borrowEntity.getType().equals(BorrowTypeEnum.Debt)) {
            return "DEBT_NOTIFICATION_MESSAGE_KEY";
        } else {
            return "LOAN_NOTIFICATION_MESSAGE_KEY";
        }
    }

    public String[] notificationLocArgs(BorrowEntity borrowEntity) {
        BigDecimal amountLeft = borrowEntity.getAmountCents().add(calculationService.returnedAmount(borrowEntity).negate());
        return new String[]
                {currencyService.getReadableAmount(amountLeft, borrowEntity.getAmountCurrency()), borrowEntity.getName()};
    }

    private void checkAndFillBorrowWithCreationForm(BorrowEntity borrowEntity, BorrowCreationForm form) {
        validationService.validateCreationForm(form);
        borrowEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
        borrowEntity.setAmountCurrency(form.getAmountCurrency() != null
                ? form.getAmountCurrency()
                : borrowEntity.getUser().getDefaultCurrency());
        borrowEntity.setBorrowedAt(form.getBorrowedAt() != null
                ? Timestamp.valueOf(form.getBorrowedAt())
                : TimeUtil.now());
        borrowEntity.setName(form.getName());
        borrowEntity.setIconUrl(form.getIconUrl());
        borrowEntity.setComment(form.getComment());
        borrowEntity.setPayday(form.getPayday() != null ? Timestamp.valueOf(form.getPayday()) : null);
        borrowEntity.setIsReturned(false);
        setCreationTimestamps(borrowEntity);
    }

    private void updateIsReturnedWithReturnedAmount(BorrowEntity borrowEntity, BigDecimal alreadyReturnedAmount) {
        borrowEntity.setIsReturned(alreadyReturnedAmount.compareTo(borrowEntity.getAmountCents()) >= 0);
        TransactionEntity borrowingTransaction = transactionService.getBorrowingTransaction(borrowEntity);
        borrowingTransaction.setIsReturned(borrowEntity.getIsReturned());
        transactionService.save(borrowingTransaction);
        save(borrowEntity);
    }

    private void setCreationTimestamps(BorrowEntity borrowEntity) {
        Timestamp now = TimeUtil.now();
        borrowEntity.setCreatedAt(now);
        borrowEntity.setUpdatedAt(now);
    }

    private void checkAndUpdateBorrowWithForm(BorrowEntity borrowEntity, BorrowUpdatingForm form) {
        validationService.validateUpdatingForm(form);
        boolean isBorrowedAtChanged = false;
        boolean isAmountChanged = false;
        boolean isNameChanged = false;
        boolean isPaydayChanged = false;
        if (form.getComment() != null) {
            borrowEntity.setComment(form.getComment());
        }
        if (form.getIconUrl() != null) {
            borrowEntity.setIconUrl(form.getIconUrl());
        }
        if (form.getAmountCents() != null && !form.getAmountCents().equals(borrowEntity.getAmountCents().longValue())) {
            borrowEntity.setAmountCents(BigDecimal.valueOf(form.getAmountCents()));
            isAmountChanged = true;
        }
        if (form.getBorrowedAt() != null && !Timestamp.valueOf(form.getBorrowedAt()).equals(borrowEntity.getBorrowedAt())) {
            borrowEntity.setBorrowedAt(Timestamp.valueOf(form.getBorrowedAt()));
            isBorrowedAtChanged = true;
        }
        if (form.getName() != null && !form.getName().isBlank() && !form.getName().equals(borrowEntity.getName())) {
            borrowEntity.setName(form.getName());
            isNameChanged = true;
        }
        if (form.getPayday() != null && !Timestamp.valueOf(form.getPayday()).equals(borrowEntity.getPayday())) {
            borrowEntity.setPayday(Timestamp.valueOf(form.getPayday()));
            isPaydayChanged = true;
        }
        updateIsReturnedWithReturnedAmount(borrowEntity, calculationService.returnedAmount(borrowEntity));
        updateReminder(borrowEntity, isPaydayChanged);
        transactionService.updateBorrowingTransaction(borrowEntity, isAmountChanged || isBorrowedAtChanged || isNameChanged);
    }

    private void updateReminder(BorrowEntity borrowEntity, boolean isNeedToUpdateReminder) {
        if (!isNeedToUpdateReminder) return;
        ReminderEntity reminderEntity = reminderService.findBySourceIdAndType(borrowEntity.getId(), RemindableTypeEnum.Borrow);
        if (reminderEntity != null) {
            reminderEntity.setStartDate(borrowEntity.getPayday());
            reminderService.save(reminderEntity);
        }
    }

    private void destroyTransactions(BorrowEntity borrowEntity) {
        Set<TransactionEntity> transactionToDestroy = transactionService.getReturningTransactions(borrowEntity);
        Timestamp now = TimeUtil.now();
        transactionToDestroy.forEach(transactionEntity -> {
            transactionEntity.setDeletedAt(now);
            transactionService.afterDestroy(transactionEntity);
            transactionService.save(transactionEntity);
        });
    }

}
