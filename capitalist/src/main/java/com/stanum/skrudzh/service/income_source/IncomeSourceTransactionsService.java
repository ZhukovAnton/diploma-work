package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
public class IncomeSourceTransactionsService extends TransactionBase {

    @Autowired
    public IncomeSourceTransactionsService(EntityUtil entityUtil,
                                           UserUtil userUtil,
                                           CurrencyService currencyService,
                                           TransactionRepository transactionRepository) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
    }

    public Set<TransactionEntity> findIncomeTransactionsInPeriod(IncomeSourceEntity incomeSourceEntity, Timestamp gotAtFrom, Timestamp gotAtTo) {
        return transactionRepository.getIncomeTransactionsForIncomeSourceInPeriod(incomeSourceEntity.getId(), gotAtFrom, gotAtTo);
    }

    public Set<TransactionEntity> findAllIncomeSourceTransactions(IncomeSourceEntity incomeSourceEntity) {
        return transactionRepository.getAllBySourceIdAndSourceType(incomeSourceEntity.getId(), EntityTypeEnum.IncomeSource.name());
    }

    public Set<TransactionEntity> findPositiveProfitTransactions(ActiveEntity activeEntity, Timestamp from, Timestamp till) {
        return transactionRepository.getPositiveProfitTransactionsInPeriod(activeEntity.getId(), from, till);
    }

}
