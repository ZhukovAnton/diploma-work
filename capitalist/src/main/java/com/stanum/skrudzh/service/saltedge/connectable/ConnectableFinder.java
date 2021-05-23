package com.stanum.skrudzh.service.saltedge.connectable;

import com.stanum.skrudzh.jpa.model.base.Connectable;
import com.stanum.skrudzh.model.enums.EntityTypeEnum;
import com.stanum.skrudzh.service.active.ActiveFinder;
import com.stanum.skrudzh.service.expense_source.ExpenseSourceFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConnectableFinder {

    private final ActiveFinder activeFinder;

    private final ExpenseSourceFinder expenseSourceFinder;

    public Connectable find(Long id, EntityTypeEnum entityType) {
        if (entityType == EntityTypeEnum.ExpenseSource) {
            return expenseSourceFinder.findById(id);
        } else if (entityType == EntityTypeEnum.Active) {
            return activeFinder.findById(id);
        } else {
            return null;
        }
    }

}
