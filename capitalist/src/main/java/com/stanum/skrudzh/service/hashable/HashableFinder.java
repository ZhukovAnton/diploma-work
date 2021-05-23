package com.stanum.skrudzh.service.hashable;

import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashableFinder {

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    public Optional<Hashable> findHashable(HashableTypeEnum hashableType, String prototypeKey, UserEntity userEntity) {
        if (hashableType.equals(HashableTypeEnum.IncomeSource)) {
            return incomeSourceFinder.findByPrototypeKey(userEntity, prototypeKey);
        } else {
            return expenseCategoryFinder.findByPrototypeKey(userEntity, prototypeKey);
        }
    }

    public Optional<Object> findHashableByTypeAndId(Long hashableId, HashableTypeEnum hashableType) {
        if (hashableType.equals(HashableTypeEnum.IncomeSource)) {
            return Optional.of(incomeSourceFinder.findById(hashableId));
        } else {
            return Optional.of(expenseCategoryFinder.findById(hashableId));
        }
    }


}
