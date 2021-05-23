package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.config.Limits;
import com.stanum.skrudzh.controller.form.ActiveCreationForm;
import com.stanum.skrudzh.controller.form.ActiveUpdatingForm;
import com.stanum.skrudzh.jpa.model.ActiveEntity;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.ActiveRepository;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.saltedge.account_connection.AccountConnectionManagementService;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActiveRequestService {

    private final ActiveRepository activeRepository;

    private final ActiveManagementService managementService;

    private final ActiveFinder finder;

    private final UserUtil userUtil;

    private final BasketFinder basketFinder;

    private final AccountConnectionManagementService accountConnectionManagementService;

    private final Limits limits;

    public ActiveEntity createActiveEntityWithCreationForm(Long userId, Long basketId, ActiveCreationForm.ActiveCF form) {
        BasketEntity basketEntity = null;
        UserEntity userEntity = null;
        if(basketId != null) {
            basketEntity = basketFinder.findBasketById(basketId);
            userUtil.checkRightAccessAcrossBasket(basketId);

            //TODO create separate subscription service
            checkSubscription(basketEntity);
        } else {
            userUtil.checkRightAccess(userId);
            userEntity = RequestUtil.getUser();
        }

        ActiveEntity activeEntity = managementService.createActiveEntityWithCreationForm(userEntity, basketEntity, form);
        accountConnectionManagementService
                .updateOrCreateAccountConnection(activeEntity, form.getAccountConnectionAttributes());
        return activeEntity;
    }

    public Set<ActiveEntity> indexActivesByBasketId(Long basketId) {
        userUtil.checkRightAccessAcrossBasket(basketId);
        return finder.findAllActivesByBasket(basketId);
    }

    public Set<ActiveEntity> indexActivesByUserId(Long userId) {
        userUtil.checkRightAccess(userId);
        return finder.findAllActivesByUser(RequestUtil.getUser());
    }

    public ActiveEntity getActiveById(Long id) {
        ActiveEntity activeEntity = finder.findById(id);
        userUtil.checkRightAccess(activeEntity.getUser() != null ? activeEntity.getUser().getId() : activeEntity.getBasketEntity().getUser().getId());
        return activeEntity;
    }

    public void updateActiveWithUpdatingForm(Long id, ActiveUpdatingForm.ActiveUF form) {
        ActiveEntity activeEntity = getActiveById(id);
        managementService.updateActiveWithUpdatingForm(activeEntity, form);
        accountConnectionManagementService
                .updateOrCreateAccountConnection(activeEntity, form.getAccountConnectionAttributes());
    }

    public void destroyActive(Long id) {
        ActiveEntity activeEntity = getActiveById(id);
        managementService.destroyActive(activeEntity, false);
    }

    private void checkSubscription(BasketEntity basketEntity) {
        if (basketEntity.getUser().getHasActiveSubscription()) return;
        if (activeRepository.countActiveEntitiesByBasketEntityAndDeletedAtIsNull(basketEntity) >= limits.getAssetsLimit()) {
            userUtil.checkSubscription(basketEntity.getUser());
        }
    }
}
