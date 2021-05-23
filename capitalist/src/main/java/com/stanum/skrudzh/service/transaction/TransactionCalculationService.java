package com.stanum.skrudzh.service.transaction;

import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class TransactionCalculationService {

    private final TransactionRepository transactionRepository;

    public Long countTransactionsInPeriod(UserEntity userEntity, Timestamp from, Timestamp till) {
        return transactionRepository.countTransactionsInPeriod(userEntity, from, till);
    }

}
