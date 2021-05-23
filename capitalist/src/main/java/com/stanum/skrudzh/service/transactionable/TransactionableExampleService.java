package com.stanum.skrudzh.service.transactionable;

import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.model.dto.TransactionableExample;
import com.stanum.skrudzh.model.dto.TransactionableExamples;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionableExampleService {

    public TransactionableExamples createTransactionableResponse(List<TransactionableExampleEntity> transactionableExampleEntities) {
        return new TransactionableExamples(transactionableExampleEntities
                .stream()
                .map(TransactionableExample::new)
                .collect(Collectors.toList()));
    }

}
