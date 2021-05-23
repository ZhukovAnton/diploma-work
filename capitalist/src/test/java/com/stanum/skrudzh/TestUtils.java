package com.stanum.skrudzh;

import com.stanum.skrudzh.controller.form.*;
import com.stanum.skrudzh.controller.form.attributes.BorrowingTransactionAttributes;
import com.stanum.skrudzh.controller.form.attributes.CreditingTransactionAttributes;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.model.enums.ProviderStatusEnum;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TestUtils {

    public static IncomeSourceCreationForm createIncomeForm(String currency, String sourceName, Long monthlyCents) {
        IncomeSourceCreationForm form = new IncomeSourceCreationForm();
        IncomeSourceCreationForm.IncomeSourceCF cf = form.new IncomeSourceCF();
        cf.setCurrency(currency);
        cf.setName(sourceName);
        cf.setMonthlyPlannedCents(monthlyCents);

        form.setIncomeSource(cf);
        return form;
    }

    public static ExpenseSourceCreationForm createExpenseForm(String currency, String name, Long amountCents) {
        ExpenseSourceCreationForm form = new ExpenseSourceCreationForm();
        ExpenseSourceCreationForm.ExpenseSourceCF cf = form.new ExpenseSourceCF();
        cf.setAmountCents(amountCents);
        cf.setCurrency(currency);
        cf.setName(name);
        form.setExpenseSource(cf);
        return form;
    }

    public static TransactionCreationForm createTransactionForm(Long amountCents, Long sourceId, Long destinationId) {
        return createTransactionForm(amountCents, sourceId, destinationId, LocalDateTime.now());
    }

    public static TransactionCreationForm createTransactionForm(Long amountCents, Long sourceId, Long destinationId, LocalDateTime gotAt) {
        TransactionCreationForm form = new TransactionCreationForm();
        TransactionCreationForm.TransactionCF cf = form.new TransactionCF();
        cf.setAmountCents(amountCents);
        cf.setSourceId(sourceId);
        cf.setDestinationId(destinationId);
        cf.setConvertedAmountCents(amountCents);
        cf.setGotAt(gotAt);
        cf.setDestinationType(EntityTypeEnum.ExpenseSource.name());
        cf.setSourceType(EntityTypeEnum.IncomeSource.name());
        form.setTransaction(cf);
        return form;
    }

    public static UserEntity create() {
        UserEntity entity = new UserEntity();
        entity.setOnBoarded(false);
        entity.setLastname("Lastname");
        entity.setEmail("myemail@mail.com");
        entity.setFirstname("firstname");
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setDefaultCurrency("RUB");
        entity.setHasActiveSubscription(true);
        entity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setDefaultPeriod(PeriodEnum.month);
        entity.setLocale("EN");
        return entity;
    }

    public static UserEntity convert(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setOnBoarded(false);
        entity.setLastname(user.getLastname());
        entity.setEmail(user.getEmail());
        entity.setFirstname(user.getFirstname());
        entity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        entity.setDefaultCurrency(user.getDefaultCurrency().getIsoCode());
        entity.setHasActiveSubscription(true);
        entity.setDefaultPeriod(user.getDefaultPeriod());
        entity.setSaltEdgeCustomerId(user.getSaltEdgeCustomerSecret());
        return entity;
    }

    public static UserCreationForm.UserCF userCreationForm(String email, String password) {
        UserCreationForm form = new UserCreationForm();
        UserCreationForm.UserCF cf = form.new UserCF();
        cf.setEmail(email);
        cf.setLastname("MyLastName");
        cf.setFirstname("MyFirstName");
        cf.setPassword(password);
        cf.setPasswordConfirmation(password);
        form.setUser(cf);
        return cf;
    }

    public static ConnectionCreationForm connectionCreateForm(String connectionId) {
        ConnectionCreationForm form = new ConnectionCreationForm();
        ConnectionCreationForm.ConnectionCF cf = new ConnectionCreationForm.ConnectionCF();
        cf.setSaltEdgeConnectionId(connectionId);
        cf.setProviderId("68");
        cf.setProviderName("Fake Bank Simple");
        cf.setProviderCode("fakebank_simple_xf");
        cf.setStatus(ProviderStatusEnum.inactive);
        form.setConnection(cf);
        return form;
    }

    public static DebtCreationForm createDebtForm(String name) {
        DebtCreationForm form = new DebtCreationForm();
        BorrowCreationForm borrowCreationForm = new BorrowCreationForm();
        borrowCreationForm.setAmountCents(100L);
        borrowCreationForm.setAmountCurrency("RUB");
        borrowCreationForm.setName(name);
        borrowCreationForm.setBorrowedAt(LocalDateTime.now());
        borrowCreationForm.setPayday(LocalDateTime.now());
        borrowCreationForm.setBorrowingTransactionAttributes(new BorrowingTransactionAttributes());
        form.debt(borrowCreationForm);
        return form;
    }

    public static CreditCreationForm createCreditForm(String name) {
        CreditCreationForm form = new CreditCreationForm();
        CreditCreationForm.CreditCF cf = form.new CreditCF();
        cf.setCurrency("RUB");
        cf.setName(name);
        cf.setAmountCents(1000L);
        cf.setReturnAmountCents(10L);
        cf.setMonthlyPaymentCents(100L);
        cf.setCreditTypeId(1L);
        cf.setCreditingTransactionAttributes(new CreditingTransactionAttributes());
        form.setCredit(cf);
        return form;
    }

    public static LoanCreationForm createLoanForm(String name) {
        LoanCreationForm form = new LoanCreationForm();
        BorrowCreationForm cf = new BorrowCreationForm();
        cf.setName(name);
        cf.setAmountCents(1000L);
        cf.setAmountCurrency("RUB");
        cf.setBorrowedAt(LocalDateTime.now());
        cf.setPayday(LocalDateTime.now());
        cf.setBorrowingTransactionAttributes(new BorrowingTransactionAttributes());
        form.setLoan(cf);
        return form;
    }

    public static ActiveCreationForm createActiveForm(String name) {
        ActiveCreationForm form = new ActiveCreationForm();
        ActiveCreationForm.ActiveCF activeCF = form.new ActiveCF();
        activeCF.setName(name);
        activeCF.setCurrency("RUB");
        activeCF.setActiveTypeId(1L);
        form.setActive(activeCF);
        return form;
    }

    public static TransactionCreationForm createTrForm(Long amountCents, Long sourceId, Long destinationId) {
        return createTrForm(amountCents, sourceId, destinationId, LocalDateTime.now());
    }

    public static  TransactionCreationForm createTrForm(Long amountCents, Long sourceId, Long destinationId, LocalDateTime gotAt) {
       return createTrForm(amountCents, sourceId, destinationId,
               EntityTypeEnum.IncomeSource.name(), EntityTypeEnum.ExpenseSource.name(), gotAt);
    }

    public static TransactionCreationForm createTrForm(Long amountCents, Long sourceId, Long destinationId,
                                                       String sourceType, String destinationtype, LocalDateTime gotAt) {
        TransactionCreationForm form = new TransactionCreationForm();
        TransactionCreationForm.TransactionCF cf = form.new TransactionCF();
        cf.setAmountCents(amountCents);
        cf.setSourceId(sourceId);
        cf.setDestinationId(destinationId);
        cf.setConvertedAmountCents(amountCents);
        cf.setGotAt(gotAt);
        cf.setAmountCurrency("RUB");
        cf.setConvertedAmountCurrency("RUB");
        cf.setDestinationType(destinationtype);
        cf.setSourceType(sourceType);
        form.setTransaction(cf);
        return form;
    }

    public static ExpenseCategoryCreationForm createExpenseCategoryForm(String name) {
        ExpenseCategoryCreationForm form = new ExpenseCategoryCreationForm();
        ExpenseCategoryCreationForm.ExpenseCategoryCF categoryCF = form.new ExpenseCategoryCF();
        categoryCF.setCurrency("RUB");
        categoryCF.setName(name);
        form.setExpenseCategory(categoryCF);
        return form;
    }
}
