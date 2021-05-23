package com.stanum.skrudzh.jpa.model.base;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;

import java.math.BigDecimal;

public interface Connectable extends Transactionable {

    AccountConnectionEntity getAccountConnectionEntity();

    void setAccountConnectionEntity(AccountConnectionEntity accountConnectionEntity);

    BigDecimal getBalance();

    void setBalance(BigDecimal newBalance);

    Integer getMaxFetchInterval();
}
