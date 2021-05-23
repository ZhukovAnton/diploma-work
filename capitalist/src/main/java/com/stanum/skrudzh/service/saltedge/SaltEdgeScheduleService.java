package com.stanum.skrudzh.service.saltedge;

import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.service.transaction.TransactionFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SaltEdgeScheduleService {

    private final TransactionFinder transactionFinder;

    private final TransactionRepository transactionRepository;

    private final SaltedgeAPI saltedgeAPI;

    @Scheduled(cron = "0 0 * * * *")
    public void removeFromDBDeletedSaltEdgeTransactions() {
        Set<TransactionEntity> destroyedSaltEdgeTransactions = transactionFinder
                .findDestroyedSaltEdgeTransactions();
        destroyedSaltEdgeTransactions.forEach(transactionRepository::delete);
    }

    @Scheduled(cron = "0 * * * * *")
    public void markDuplicatedTransactionsInsideSaltEdge() {
        Set<TransactionEntity> actualDuplicatedTransactions = transactionFinder.findActualDuplications();
        Map<UserEntity, List<String>> usersTransactions = new HashMap<>();
        actualDuplicatedTransactions.forEach(transactionEntity -> {
            if (transactionEntity.getSaltEdgeTransactionId() == null) return;
            if (usersTransactions.containsKey(transactionEntity.getUser())) {
                usersTransactions.get(transactionEntity.getUser()).add(transactionEntity.getSaltEdgeTransactionId());
            } else {
                List<String> usersSaltEdgeTransactionIds = new ArrayList<>();
                usersSaltEdgeTransactionIds.add(transactionEntity.getSaltEdgeTransactionId());
                usersTransactions.put(transactionEntity.getUser(), usersSaltEdgeTransactionIds);
            }
            transactionEntity.setIsDuplicationActual(false);
            transactionRepository.save(transactionEntity); //Bad style...
        });
        usersTransactions.forEach((user, transactions) ->
                saltedgeAPI.transaction.duplicate(user.getSaltEdgeCustomerId(), transactions));

    }

}
