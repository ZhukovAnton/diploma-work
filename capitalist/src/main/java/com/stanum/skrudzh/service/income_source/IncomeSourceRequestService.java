package com.stanum.skrudzh.service.income_source;

import com.stanum.skrudzh.controller.form.IncomeSourceCreationForm;
import com.stanum.skrudzh.controller.form.IncomeSourceUpdatingForm;
import com.stanum.skrudzh.jpa.model.IncomeSourceEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.IncomeSourcesRepository;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeSourceRequestService {

    protected final IncomeSourcesRepository incomeSourcesRepository;

    protected final UserUtil userUtil;

    private final IncomeSourceManagementService managementService;

    private final IncomeSourceFinder incomeSourceFinder;

    public IncomeSourceEntity createIncomeSourceEntityWithForm(Long userId, IncomeSourceCreationForm.IncomeSourceCF payload) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return managementService.createIncomeSourceEntityWithForm(userEntity, payload);
    }

    public List<IncomeSourceEntity> indexIncomeSources(Long userId, boolean noBorrows, boolean isIncomePlanned) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        //TODO: rework
        if (noBorrows) {
            return incomeSourceFinder
                    .findAllByUserAndIsBorrowAndIsPlanned(userEntity, false, isIncomePlanned);
        } else {
            return incomeSourceFinder
                    .findAllByUserAndIsPlanned(userEntity, isIncomePlanned);
        }
    }

    public IncomeSourceEntity getIncomeSourceById(Long id) {
        IncomeSourceEntity incomeSourceEntity = incomeSourceFinder.findById(id);
        userUtil.checkRightAccess(incomeSourceEntity.getUser().getId());
        return incomeSourceEntity;
    }

    public void updateIncomeSourceWithForm(IncomeSourceUpdatingForm.IncomeSourceUF form,
                                           Long id,
                                           boolean patch) {
        IncomeSourceEntity incomeSourceEntity = getIncomeSourceById(id);
        managementService.updateIncomeSourceWithForm(incomeSourceEntity, form, patch);
    }

    public void destroyIncomeSource(Long id) {
        IncomeSourceEntity incomeSourceEntity = getIncomeSourceById(id);
        managementService.destroyIncomeSource(incomeSourceEntity, false);
    }

    public IncomeSourceEntity getFirstBorrowIncomeSource(Long userId, String currencyCode) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return incomeSourceFinder.findBorrowIncomeSource(userEntity, currencyCode)
                .orElseGet(() -> managementService.createBorrowIncomeSource(userEntity, currencyCode));
    }

}
