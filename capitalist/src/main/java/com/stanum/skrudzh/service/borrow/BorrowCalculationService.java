package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowCalculationService {

    private final BorrowTransactionService transactionService;

    private final ExchangeService exchangeService;

    public BigDecimal returnedAmount(BorrowEntity borrowEntity) {
        Set<TransactionEntity> returnedTransactions = transactionService.getReturningTransactions(borrowEntity);
        return returnedTransactions.stream().map(transactionEntity -> {
                    if (borrowEntity.getType().equals(BorrowTypeEnum.Debt)) {
                        return transactionEntity.getConvertedAmountCurrency().equals(borrowEntity.getAmountCurrency())
                                ? transactionEntity.getConvertedAmountCents()
                                : exchangeService.exchange(transactionEntity.getConvertedAmountCurrency(),
                                borrowEntity.getAmountCurrency(), transactionEntity.getConvertedAmountCents());
                    } else {
                        return transactionEntity.getAmountCurrency().equals(borrowEntity.getAmountCurrency())
                                ? transactionEntity.getAmountCents()
                                : exchangeService.exchange(transactionEntity.getAmountCurrency(),
                                borrowEntity.getAmountCurrency(), transactionEntity.getAmountCents());
                    }
                }
        )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
