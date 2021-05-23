package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.controller.form.BorrowCreationForm;
import com.stanum.skrudzh.controller.form.BorrowUpdatingForm;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowRequestService {

    private final UserUtil userUtil;

    private final BorrowFinder finder;

    private final BorrowManagementService managementService;

    public Set<BorrowEntity> indexBorrowsByUserId(Long userId, BorrowTypeEnum borrowType) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        return finder.findNotReturnedBorrows(userEntity, borrowType);
    }

    public BorrowEntity createBorrowWithCreationForm(Long userId, BorrowCreationForm form, BorrowTypeEnum borrowType) {
        userUtil.checkRightAccess(userId);
        UserEntity userEntity = RequestUtil.getUser();
        userUtil.checkSubscription(userEntity);
        return managementService.createBorrowWithCreationForm(userEntity, form, borrowType);
    }

    public BorrowEntity getBorrowById(Long id) {
        BorrowEntity borrowEntity = finder.findById(id);
        userUtil.checkRightAccess(borrowEntity.getUser().getId());
        return borrowEntity;
    }

    public void updateBorrow(Long id, BorrowUpdatingForm form) {
        BorrowEntity borrowEntity = getBorrowById(id);
        managementService.updateBorrow(borrowEntity, form);
    }

    public void destroyBorrow(Long id) {
        BorrowEntity borrowEntity = getBorrowById(id);
        managementService.destroyBorrow(borrowEntity, false);
    }
}
