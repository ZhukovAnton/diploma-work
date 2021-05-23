package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.service.exchange_rate.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreditCalculationService {

    private final CreditTransactionsService transactionsService;

    private final ExchangeService exchangeService;

    public BigDecimal paidAmount(CreditEntity creditEntity) {
        if (creditEntity.getId() == null) return creditEntity.getAlreadyPaidCents();
        Set<TransactionEntity> payingCreditTransactions = transactionsService.getCreditPayTransactions(creditEntity);
        BigDecimal paidCreditAmount = payingCreditTransactions.stream()
                .map(transactionEntity -> {
                    if (transactionEntity.getConvertedAmountCurrency().equals(creditEntity.getCurrency())) {
                        return transactionEntity.getConvertedAmountCents();
                    } else {
                        return exchangeService.exchange(
                                transactionEntity.getConvertedAmountCurrency(),
                                creditEntity.getCurrency(),
                                transactionEntity.getConvertedAmountCents());
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return paidCreditAmount.add(creditEntity.getAlreadyPaidCents());
    }

}
