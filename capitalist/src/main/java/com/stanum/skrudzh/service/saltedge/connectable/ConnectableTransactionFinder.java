package com.stanum.skrudzh.service.saltedge.connectable;

import com.stanum.skrudzh.jpa.model.base.Transactionable;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.service.TransactionBase;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectableTransactionFinder extends TransactionBase {

    public ConnectableTransactionFinder(TransactionRepository transactionRepository, EntityUtil entityUtil, UserUtil userUtil, CurrencyService currencyService) {
        super(transactionRepository, entityUtil, userUtil, currencyService);
    }

    public Optional<Timestamp> findLastBankTransactionGotAt(Transactionable entity) {
        return transactionRepository.getLastBankTransactionGotAt(entity.getId(), entity.getEntityType().name());
    }

    public Optional<Timestamp> findFirstPendingGotAtInPeriod(Transactionable entity, Timestamp from, Timestamp till) {
        List<Timestamp> firstPendingGotAtInPeriod =
                transactionRepository.getFirstPendingGotAtInPeriod(entity.getId(), entity.getEntityType().name(), from, till);

        if(firstPendingGotAtInPeriod.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(firstPendingGotAtInPeriod.get(0));
        }
    }
}
