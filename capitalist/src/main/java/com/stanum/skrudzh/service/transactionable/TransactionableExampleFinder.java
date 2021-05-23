package com.stanum.skrudzh.service.transactionable;

import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.jpa.repository.TransactionableExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionableExampleFinder {

    private final TransactionableExampleRepository transactionableExampleRepository;


    public List<TransactionableExampleEntity> findTransactionableExamples(String transactionableType, String basketType, String country) {
        List<TransactionableExampleEntity> transactionableExampleEntities =
                transactionableExampleRepository.findAllByCountry(country);
        return transactionableExampleEntities
                .stream()
                .filter(transactionableExampleEntity ->
                        basketType == null
                                || transactionableExampleEntity.getBasketType() != null
                                && transactionableExampleEntity.getBasketType().name()
                                .equals(basketType))
                .filter(transactionableExampleEntity ->
                        transactionableType == null
                                || transactionableExampleEntity.getTransactionableType()
                                .equals(transactionableType))
                .collect(Collectors.toList());
    }

    public Optional<TransactionableExampleEntity> findTransactionableExpampleByPropertyKey(String prototypeKey, String country) {
        return transactionableExampleRepository.findByPrototypeKeyAndCountry(prototypeKey, country);
    }

}
