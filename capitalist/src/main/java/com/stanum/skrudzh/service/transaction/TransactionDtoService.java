package com.stanum.skrudzh.service.transaction;

import com.stanum.skrudzh.jpa.model.*;
import com.stanum.skrudzh.model.dto.Transaction;
import com.stanum.skrudzh.model.dto.Transactions;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.active.ActiveDtoService;
import com.stanum.skrudzh.service.borrow.BorrowDtoService;
import com.stanum.skrudzh.service.credit.CreditDtoService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.income_source.IncomeSourceFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionDtoService {

    private final IncomeSourceFinder incomeSourceFinder;

    private final CreditDtoService creditDtoService;

    private final BorrowDtoService borrowDtoService;

    private final ActiveDtoService activeDtoService;

    public Transactions createTransactionsResponse(List<TransactionEntity> transactionEntities) {
        return new Transactions(transactionEntities
                .stream()
                .map(this::createTransactionResponse)
                .collect(Collectors.toList()));
    }

    public Transaction createTransactionResponse(TransactionEntity transactionEntity) {
        Transaction transactionDto = new Transaction(transactionEntity);

        CreditEntity creditEntity = transactionEntity.getCredit();
        BorrowEntity borrowEntity = transactionEntity.getBorrow();
        BorrowEntity returningBorrow = transactionEntity.getReturningBorrow();
        ActiveEntity activeEntity = transactionEntity.getActiveEntity();
        if (transactionEntity.getSourceType().equals(EntityTypeEnum.IncomeSource.name())) {
            IncomeSourceEntity incomeSourceEntity = incomeSourceFinder.findById(transactionEntity.getSourceId());
            if (incomeSourceEntity.getActive() != null) {
                transactionDto.setSourceActiveId(incomeSourceEntity.getActive().getId());
                transactionDto.setSourceActiveIconUrl(incomeSourceEntity.getActive().getIconUrl());
                transactionDto.setSourceActiveTitle(incomeSourceEntity.getActive().getName());
            }
        }
        if (transactionEntity.getSourceType().equals(EntityTypeEnum.Active.name())) {
            IncomeSourceEntity activeIncomeSource = incomeSourceFinder.findByActiveId(transactionEntity.getSourceId());
            if (activeIncomeSource != null) {
                transactionDto
                        .setSourceIncomeSourceId(activeIncomeSource.getId());
            }
        }
        transactionDto.setCredit(creditEntity != null ? creditDtoService.createCreditResponse(transactionEntity.getCredit()) : null);
        transactionDto.setBorrow(borrowEntity != null ? borrowDtoService.createBorrowResponse(borrowEntity) : null);
        transactionDto.setReturningBorrow(returningBorrow != null ? borrowDtoService.createBorrowResponse(returningBorrow) : null);
        transactionDto.setActive(activeEntity != null ? activeDtoService.createActiveDto(activeEntity) : null);
        transactionDto.setAmountCurrency(CurrencyService.getCurrencyByIsoCode(transactionEntity.getAmountCurrency()));
        transactionDto.setConvertedAmountCurrency(CurrencyService.getCurrencyByIsoCode(transactionEntity.getConvertedAmountCurrency()));
        return transactionDto;
    }

}
