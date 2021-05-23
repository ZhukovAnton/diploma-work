package com.stanum.skrudzh.service.order.migration;

import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.Rankable;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.borrow.BorrowFinder;
import com.stanum.skrudzh.service.order.OrderService;
import com.stanum.skrudzh.model.enums.OrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMigrator {

    private final BorrowFinder borrowFinder;

    private final ActiveFinder activeFinder;

    private final OrderService orderService;

    public void migrateOrders(UserEntity user) {
        log.info("Migrate order entities for user with id {}", user.getId());
        Set<BorrowEntity> debts = borrowFinder.findAllByUserAndType(user, BorrowTypeEnum.Debt);

        Set<ActiveEntity> actives = activeFinder.findAllActivesByUser(user);
        List<ActiveEntity> sortedActives = actives.stream()
                .sorted(Comparator.nullsLast(Comparator.comparing(Rankable::getRowOrder)))
                .collect(Collectors.toList());

        int index = 0;
        for(ActiveEntity active : sortedActives) {
            log.info("Save order for ACTIVE with index = {}, id = {}", index, active.getId());
            orderService.saveOrder(user, OrderType.ACTIVE_BORROW, EntityTypeEnum.Active, active.getId(), index);
            index++;
        }

        for(BorrowEntity borrow : debts) {
            log.info("Save order for DEBT with index = {}, id = {}", index, borrow.getId());
            orderService.saveOrder(user, OrderType.ACTIVE_BORROW, EntityTypeEnum.Borrow, borrow.getId(), index);
            index++;
        }
    }
}
