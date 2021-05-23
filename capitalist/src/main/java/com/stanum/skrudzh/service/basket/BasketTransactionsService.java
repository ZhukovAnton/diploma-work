package com.stanum.skrudzh.service.basket;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.model.enums.PeriodEnum;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Service
public class BasketTransactionsService extends TransactionBase {

    private final ExchangeService exchangeService;

    @Autowired
    public BasketTransactionsService(ExchangeService exchangeService,
                                     EntityUtil entityUtil,
                                     UserUtil userUtil,
                                     CurrencyService currencyService,
                                     TransactionRepository transactionRepository) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
        this.exchangeService = exchangeService;
    }

    public Set<TransactionEntity> getExpenseCategoriesTransactionsAtPeriod(BasketEntity basketEntity, PeriodEnum period, boolean withVirtual) {
        Timestamp from = RequestUtil.getBeginningOfPeriod(period);
        Timestamp to = RequestUtil.getEndOfPeriod(period);
        return transactionRepository
                .getAllJoyBasketTransactionsWithinPeriod(basketEntity,
                        withVirtual,
                        from,
                        to);
    }


    public Set<TransactionEntity> getActiveTransactionsAtPeriod(BasketEntity basketEntity, PeriodEnum period) {
        Timestamp from = RequestUtil.getBeginningOfPeriod(period);
        Timestamp to = RequestUtil.getEndOfPeriod(period);
        return transactionRepository
                .getBuyOrExpenseActiveTransactionsWithinPeriod(basketEntity,
                        from,
                        to);
    }

    public BigDecimal getUserAmountByTransaction(TransactionEntity transactionEntity, UserEntity userEntity) {
        if (transactionEntity.getConvertedAmountCurrency().equals(userEntity.getDefaultCurrency())) {
            return transactionEntity.getConvertedAmountCents();
        }
        if (transactionEntity.getAmountCurrency().equals(userEntity.getDefaultCurrency())) {
            return transactionEntity.getAmountCents();
        }
        if (isSourceReduceDestinationGrowth(transactionEntity)
                || transactionEntity.getSourceType().equals(EntityTypeEnum.IncomeSource.name())) {
            return exchangeService.exchange(transactionEntity.getConvertedAmountCurrency(),
                    userEntity.getDefaultCurrency(),
                    transactionEntity.getConvertedAmountCents());
        } else {
            return exchangeService.exchange(transactionEntity.getAmountCurrency(),
                    userEntity.getDefaultCurrency(),
                    transactionEntity.getAmountCents());
        }
    }
}
