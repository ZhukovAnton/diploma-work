package com.stanum.skrudzh.service.transactionable;

import com.stanum.skrudzh.jpa.model.TransactionableExampleEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.expense_category.ExpenseCategoryFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import com.stanum.skrudzh.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionableExampleRequestService {

    private final TransactionableExampleFinder finder;

    private final ExpenseCategoryFinder expenseCategoryFinder;

    private final IncomeSourceFinder incomeSourceFinder;

    private final ExpenseSourceFinder expenseSourceFinder;

    public List<TransactionableExampleEntity> getPrototypesByParams(String transactionableType,
                                                                    String basketType,
                                                                    String country,
                                                                    Boolean isUsed) {
        country = country != null ? country : RequestUtil.getRegion();
        if (transactionableType != null && transactionableType.equals(EntityTypeEnum.ExpenseCategory.name())
                && isUsed != null) {
            Set<String> allUsedPrototypeKeys = expenseCategoryFinder.findAllUsedPrototypeKeys(RequestUtil.getUser());
            List<TransactionableExampleEntity> expenseCategoryPrototypes = finder
                    .findTransactionableExamples(transactionableType, basketType, country);
            return expenseCategoryPrototypes
                    .stream()
                    .filter(prototype -> allUsedPrototypeKeys.contains(prototype.getPrototypeKey()) == isUsed)
                    .collect(Collectors.toList());
        } else if (transactionableType != null && transactionableType.equals(EntityTypeEnum.IncomeSource.name())
                && isUsed != null) {
            Set<String> allUsedPrototypeKeys = incomeSourceFinder.findAllUsedPrototypeKeys(RequestUtil.getUser());
            return finder.findTransactionableExamples(transactionableType, basketType, country)
                    .stream()
                    .filter(prototype -> allUsedPrototypeKeys.contains(prototype.getPrototypeKey()) == isUsed)
                    .collect(Collectors.toList());
        } else if (transactionableType != null && transactionableType.equals(EntityTypeEnum.ExpenseSource.name())
                    && isUsed != null) {
            Set<String> allUsedPrototypeKeys = expenseSourceFinder.findAllUsedPrototypeKeys(RequestUtil.getUser());
            return finder.findTransactionableExamples(transactionableType, basketType, country)
                    .stream()
                    .filter(prototype -> allUsedPrototypeKeys.contains(prototype.getPrototypeKey()) == isUsed)
                    .collect(Collectors.toList());
        } else {
            return finder.findTransactionableExamples(transactionableType, basketType, country);
        }
    }

}
