package com.stanum.skrudzh.service.saltedge.learn;

import com.stanum.skrudzh.jpa.model.HashEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.saltage.SaltedgeAPI;
import com.stanum.skrudzh.service.hash.HashFinder;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LearnSaltedgeServiceImpl implements LearnSaltedgeService {

    @Autowired
    private EntityUtil entityUtil;

    @Autowired
    private SaltedgeAPI saltedgeAPI;

    @Autowired
    private HashFinder hashFinder;

    @Override
    public void learn(TransactionEntity transactionEntity) {
        Long trId = transactionEntity.getId();
        log.info("[Learn SaltEdge] by trId={}", trId);
        if(transactionEntity.getSaltEdgeTransactionId() == null) {
            log.info("[Learn SaltEdge] trId={} is not SaltEdge tr", trId);
            return;
        }

        Object target = null;
        if(EntityTypeEnum.ExpenseSource.name().equals(transactionEntity.getSourceType())) {
            log.info("[Learn SaltEdge] Learn ExpenseSource for trId={}", trId);
            target = entityUtil.find(transactionEntity.getSourceId(), EntityTypeEnum.valueOf(transactionEntity.getSourceType()));
        } else if(EntityTypeEnum.IncomeSource.name().equals(transactionEntity.getSourceType())) {
            log.info("[Learn SaltEdge] Learn IncomeSource for trId={}", trId);
            target = entityUtil.find(transactionEntity.getDestinationId(), EntityTypeEnum.valueOf(transactionEntity.getDestinationType()));
        }
        learn(target, transactionEntity);
    }

    private void learn(Object object, TransactionEntity transactionEntity) {
        if(object instanceof Hashable) {
            String prototypeKey = ((Hashable) object).getPrototypeKey();
            log.info("[Learn SaltEdge] for Hashable object with id = {}, type={}, prototypeKey={}",
                    ((Hashable) object).getId(), ((Hashable) object).getHashableType(), prototypeKey);

            if(prototypeKey != null) {
                Optional<HashEntity> hash = hashFinder.findByUserEntityIdAAndPrototypeKey(transactionEntity.getUser().getId(), prototypeKey);
                if (hash.isPresent()) {
                    String category = hash.get().getSaltEdgeCategory();
                    if (category != null && !category.isEmpty()) {
                        saltedgeAPI.learn.learn(transactionEntity.getUser().getSaltEdgeCustomerId(),
                                transactionEntity.getSaltEdgeTransactionId(), getMainCategory(hash.get().getSaltEdgeCategory()));
                    }
                }
            }
        }
    }

    private String getMainCategory(String result) {
        log.info("[Learn SaltEdge] Get main category from {}", result);
        int endIndex = result.indexOf(" ");
        if(endIndex < 0) {
            return result;
        } else {
            return result.substring(0, endIndex);
        }
    }
}
