package com.stanum.skrudzh.service.saltedge.account;

import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.AccountEntity;
import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.jpa.model.TransactionEntity;
import com.stanum.skrudzh.jpa.repository.AccountRepository;
import com.stanum.skrudzh.model.enums.AccountNatureEnum;
import com.stanum.skrudzh.model.enums.CardTypeEnum;
import com.stanum.skrudzh.model.enums.NatureTypeEnum;
import com.stanum.skrudzh.saltage.model.Account;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.saltedge.SaltEdgeTransactionService;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionFinder;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import com.stanum.skrudzh.utils.constant.Constants;
import com.stanum.skrudzh.utils.logic.CardNumberUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountManagementService {

    private final AccountRepository accountRepository;

    private final AccountFinder accountFinder;

    private final AccountConnectionFinder accountConnectionFinder;

    private final AccountConnectionManagementService accountConnectionManagementService;

    private final SaltEdgeTransactionService transactionService;

    public void refreshAccounts(List<Account> accounts, ConnectionEntity connectionEntity) {
        log.info("Refresh accounts for connectionEntity id = {}", connectionEntity.getId());
        Set<String> saltEdgeAccountIds = accountFinder.findAccountsByConnection(connectionEntity)
                .stream()
                .map(AccountEntity::getAccountId)
                .collect(Collectors.toSet());
        accounts.forEach(account -> {
            AccountEntity accountEntity;
            Optional<AccountConnectionEntity> accountsConnectionOptional = Optional.empty();
            if (!saltEdgeAccountIds.contains(account.getId())) {
                accountEntity = transformSaltEdgeEntityIntoApiEntity(account);
                accountEntity.setConnectionEntity(connectionEntity);
            } else {
                accountEntity = accountFinder.findBySaltedgeAccountId(account.getId());
                accountsConnectionOptional = getAccountsConnection(accountEntity);
                updateExistingAccountEntity(accountEntity, account);
            }
            save(accountEntity);
            accountsConnectionOptional.ifPresent(accountConnectionEntity -> accountConnectionManagementService
                    .updateAccountConnectionWithAccount(
                            accountConnectionEntity,
                            accountEntity,
                            account.getTransactions()));
        });
    }

    public void reconnectAccounts(List<Account> accounts, ConnectionEntity connectionEntity) {
        Set<AccountEntity> accountEntities = accountFinder.findAccountsByConnection(connectionEntity);
        accountEntities.forEach(accountEntity -> {
            Optional<Account> accountWithTheSameName = accounts.stream()
                    .filter(account -> account.getName().equals(accountEntity.getAccountName()))
                    .findAny();
            accountWithTheSameName.ifPresent(account -> {
                updateExistingAccountEntity(accountEntity, account);
                save(accountEntity);
            });
        });
    }

    public void destroyAccount(AccountEntity accountEntity, boolean withTransactions) {
        if (withTransactions) {
            Set<TransactionEntity> accountTransactions = transactionService.findTransactionsByAccount(accountEntity);
            accountTransactions.forEach(transactionEntity -> {
                transactionEntity.setAccountEntity(null);
                transactionService.save(transactionEntity);
            });
        }
        Optional<AccountConnectionEntity> accountConnectionEntityOptional =
                getAccountsConnection(accountEntity);
        accountConnectionEntityOptional.ifPresent(accountConnectionManagementService::destroyAccountConnection);
        accountRepository.delete(accountEntity);
    }

    public NatureTypeEnum getNatureType(AccountEntity accountEntity) {
        AccountNatureEnum nature = accountEntity.getNature();
        if (nature.equals(AccountNatureEnum.investment)
                || nature.equals(AccountNatureEnum.savings)) {
            return NatureTypeEnum.investment;
        } else if (nature.equals(AccountNatureEnum.account)
                || nature.equals(AccountNatureEnum.bonus)
                || nature.equals(AccountNatureEnum.card)
                || nature.equals(AccountNatureEnum.credit)
                || nature.equals(AccountNatureEnum.credit_card)
                || nature.equals(AccountNatureEnum.debit_card)
                || nature.equals(AccountNatureEnum.ewallet)
                || nature.equals(AccountNatureEnum.checking)) {
            return NatureTypeEnum.account;
        } else {
            return NatureTypeEnum.undefined;
        }
    }

    private void save(AccountEntity accountEntity) {
        accountRepository.save(accountEntity);
    }

    private AccountEntity transformSaltEdgeEntityIntoApiEntity(Account account) {
        AccountEntity accountEntity = new AccountEntity();
        fillAccountEntityWithSaltEdgeAccountData(accountEntity, account);
        accountEntity.setCreatedAt(getOrNow(account.getCreatedAt()));
        accountEntity.setUpdatedAt(getOrNow(account.getUpdatedAt()));
        return accountEntity;
    }

    private Timestamp getOrNow(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : TimeUtil.now();
    }

    private Optional<AccountConnectionEntity> getAccountsConnection(AccountEntity accountEntity) {
        return accountConnectionFinder.findByAccount(accountEntity);
    }

    private void updateExistingAccountEntity(AccountEntity accountEntity, Account saltEdgeAccount) {
        fillAccountEntityWithSaltEdgeAccountData(accountEntity, saltEdgeAccount);
        accountEntity.setUpdatedAt(TimeUtil.now());
    }

    private void fillAccountEntityWithSaltEdgeAccountData(AccountEntity accountEntity, Account saltEdgeAccount) {
        accountEntity.setAccountId(saltEdgeAccount.getId());
        accountEntity.setAccountName(saltEdgeAccount.getName());
        try {
            accountEntity.setNature(AccountNatureEnum.valueOf(saltEdgeAccount.getNature()));
        } catch (IllegalArgumentException ex) {
            log.error(ex.getMessage(), ex);
        }
        accountEntity.setBalance(CurrencyService.getAmountInCents(saltEdgeAccount.getBalance(), saltEdgeAccount.getCurrencyCode()));
        accountEntity.setCurrencyCode(saltEdgeAccount.getCurrencyCode());
        accountEntity.setAccountFullName(saltEdgeAccount.getExtra() != null ? saltEdgeAccount.getExtra().getAccountName() : null);
        accountEntity.setCardType(getCardType(saltEdgeAccount));
        accountEntity.setCards(getCardNumbers(saltEdgeAccount));
        accountEntity.setCreditLimit(saltEdgeAccount.getExtra() != null ? saltEdgeAccount.getExtra().getCreditLimit() : null);
        accountEntity.setStatus(saltEdgeAccount.getExtra() != null ? saltEdgeAccount.getExtra().getStatus() : null);
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getInterestIncome() != null) {
            accountEntity.setInterestIncome(saltEdgeAccount.getExtra().getInterestIncome().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getInterestAmount() != null) {
            accountEntity.setInterestAmount(saltEdgeAccount.getExtra().getInterestAmount().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getProfitAmount() != null) {
            accountEntity.setProfitAmount(saltEdgeAccount.getExtra().getProfitAmount().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getProfitRate() != null) {
            accountEntity.setProfitRate(saltEdgeAccount.getExtra().getProfitRate().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getAssetClass() != null) {
            accountEntity.setAssetClass(saltEdgeAccount.getExtra().getAssetClass().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getProductType() != null) {
            accountEntity.setProductType(saltEdgeAccount.getExtra().getProductType().longValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingInvestmentPercentage(saltEdgeAccount.getExtra().getFundHoldings().getInvestmentPercentage());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingBalance(saltEdgeAccount.getExtra().getFundHoldings().getBalance());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingBidPrice(saltEdgeAccount.getExtra().getFundHoldings().getBidPrice());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingValue(saltEdgeAccount.getExtra().getFundHoldings().getValue());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null
                && saltEdgeAccount.getExtra().getFundHoldings().getValueDate() != null) {
            accountEntity.setFundHoldingValueDate(Timestamp
                    .valueOf(saltEdgeAccount.getExtra().getFundHoldings().getValueDate().atStartOfDay()));
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingTotalQuality(saltEdgeAccount.getExtra().getFundHoldings().getTotalQuantity());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingAvailableQuality(saltEdgeAccount.getExtra().getFundHoldings().getAvailableQuantity());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingActualPrice(saltEdgeAccount.getExtra().getFundHoldings().getActualPrice());
        }
        if (saltEdgeAccount.getExtra() != null && saltEdgeAccount.getExtra().getFundHoldings() != null) {
            accountEntity.setFundHoldingActualValue(saltEdgeAccount.getExtra().getFundHoldings().getActualValue());
        }
    }

    private CardTypeEnum getCardType(Account saltEdgeAccount) {
        CardTypeEnum cardType = saltEdgeAccount.getExtra() != null
                ? saltEdgeAccount.getExtra().getCardType() != null
                ? CardTypeEnum.valueOf(saltEdgeAccount.getExtra().getCardType())
                : null
                : null;
        if (cardType == null && saltEdgeAccount.getExtra() != null
                && saltEdgeAccount.getExtra().getCards() != null && saltEdgeAccount.getExtra().getCards().length > 0) {
            cardType = Arrays.stream(saltEdgeAccount.getExtra().getCards())
                    .map(CardTypeEnum::detect)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(null);
        }
        return cardType;
    }

    private String getCardNumbers(Account saltEdgeAccount) {
        if(saltEdgeAccount.getExtra() == null || saltEdgeAccount.getExtra().getCards() == null) {
            return null;
        }

        String[] cardNumbers = saltEdgeAccount.getExtra().getCards();
        return Arrays.stream(cardNumbers)
                .map(CardNumberUtil::getFormattedCardNumber)
                .collect(Collectors.joining(Constants.CARDS_DELIMITER));
    }

}
