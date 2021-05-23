package com.stanum.skrudzh.service.saltedge.connectable;

import com.stanum.skrudzh.config.Limits;
import com.stanum.skrudzh.jpa.model.AccountConnectionEntity;
import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.model.enums.LastStageStatusEnum;
import com.stanum.skrudzh.utils.logic.EntityUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectableCalculationService {

    private final Limits limits;

    private final ConnectableTransactionFinder transactionFinder;

    private final EntityUtil entityUtil;

    public Timestamp calculateFetchFromDate(Connectable connectable) {
        AccountConnectionEntity accountConnection = connectable.getAccountConnectionEntity();
        Optional<Timestamp> lastBankTransactionGotAt = transactionFinder.findLastBankTransactionGotAt(connectable);
        Optional<Timestamp> lastRegularTransactionGotAt = transactionFinder.findLastRegularTransactionGotAt(connectable);
        Timestamp result;
        if (calculateByPossibleAt(accountConnection)) {
            log.debug("Calculate fetch time by possibleAt for id={}, entityType={}", connectable.getId(), connectable.getEntityType());
            Timestamp from = Timestamp.valueOf(
                    TimeUtil.roleDayToTheBeginning(
                            LocalDateTime.now(ZoneId.of("Z"))
                                    .minusDays(limits.getAvailablePendingDays())));
            Timestamp till = TimeUtil.now();
            Optional<Timestamp> firstPendingGotAtOptional = transactionFinder
                    .findFirstPendingGotAtInPeriod(connectable, from, till);
            result =  firstPendingGotAtOptional
                    .or(() -> lastBankTransactionGotAt)
                    .or(() -> lastRegularTransactionGotAt)
                    .orElseGet(() -> entityUtil.getFetchDataFrom(connectable));
        } else if (lastBankTransactionGotAt.isPresent() || lastRegularTransactionGotAt.isPresent()) {
            log.debug("Calculate fetch time by last transaction for id={}, entityType={}", connectable.getId(), connectable.getEntityType());
            result = lastBankTransactionGotAt
                    .orElseGet(() -> lastRegularTransactionGotAt
                            .orElseGet(() -> entityUtil.getFetchDataFrom(connectable)));
        } else {
            log.debug("Calculate fetch time by max fetch interval for id={}, entityType={}", connectable.getId(), connectable.getEntityType());
            result = entityUtil.getFetchDataFrom(connectable);
        }

        log.debug("Fetch time = {} for id={}, entityType={}", result, connectable.getId(), connectable.getEntityType());
        return result;
    }

    private boolean calculateByPossibleAt(AccountConnectionEntity accountConnection) {
        return accountConnection != null &&
                accountConnection.getConnectionEntity() != null &&
                accountConnection.getConnectionEntity().getLastStageStatus() != null &&
                accountConnection.getConnectionEntity().getLastStageStatus()
                        .equals(LastStageStatusEnum.finish) &&
                accountConnection.getConnectionEntity().getNextRefreshPossibleAt() != null &&
                accountConnection.getConnectionEntity().getNextRefreshPossibleAt()
                        .before(TimeUtil.now());
    }

}
