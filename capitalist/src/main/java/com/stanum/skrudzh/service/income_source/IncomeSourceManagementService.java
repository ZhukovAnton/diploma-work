package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.jpa.model.*;

import java.math.BigDecimal;
import java.util.Optional;

public interface IncomeSourceManagementService {

    IncomeSourceEntity createIncomeSourceEntityWithForm(UserEntity userEntity, IncomeSourceCreationForm.IncomeSourceCF payload);

    IncomeSourceEntity createIncomeSourceEntityByUser(UserEntity userEntity, String currencyCode);

    IncomeSourceEntity createIncomeSourceByActive(ActiveEntity activeEntity);

    void updateIncomeSourceWithForm(IncomeSourceEntity incomeSourceEntity, IncomeSourceUpdatingForm.IncomeSourceUF form, boolean patch);

    void destroyIncomeSource(IncomeSourceEntity incomeSourceEntity, boolean destroyTransactions);

    void updateIncomeSourceByActive(IncomeSourceEntity incomeSourceEntity,
                                           ActiveEntity activeEntity,
                                           BigDecimal newMonthlyPlanned);

    void updateMonthlyPlannedIncomeByActive(IncomeSourceEntity incomeSourceEntity,
                                                   BigDecimal newMonthlyPlanned);

    void destroyActiveIncomeSource(ActiveEntity activeEntity, boolean destroyWithTransactions);

    IncomeSourceEntity save(IncomeSourceEntity incomeSource);

    String notificationLocKey();

    String[] notificationLocArgs(IncomeSourceEntity incomeSourceEntity);

    IncomeSourceEntity createBorrowIncomeSource(UserEntity userEntity, String currencyCode);

    IncomeSourceEntity createVirtualIncomeSource(UserEntity userEntity, String currencyCode);

    Optional<IncomeSourceEntity> createIncomeSourceFromPrototypeWithTransaction(UserEntity userEntity,
                                                                                       String currencyCode,
                                                                                       String prototypeKey);

    void createDefaultIncomeSource(UserEntity userEntity, TransactionableExampleEntity incomeTransactionalEntity);
}
