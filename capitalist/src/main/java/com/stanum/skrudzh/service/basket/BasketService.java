package com.stanum.skrudzh.service.basket;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.BasketRepository;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.utils.TimeUtil;
import com.stanum.skrudzh.utils.logic.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;

    private final BasketFinder basketFinder;

    private final UserUtil userUtil;

    public void createBasketByType(BasketTypeEnum basketType, UserEntity userEntity) {
        BasketEntity basketEntity = new BasketEntity();
        basketEntity.setBasketType(basketType);
        basketEntity.setUser(userEntity);
        setCreationTimestamps(basketEntity);
        basketRepository.save(basketEntity);
    }

    public BasketEntity getBasketByType(BasketTypeEnum basketType, Set<BasketEntity> baskets) {
        return baskets
                .stream()
                .filter(basket -> basket.getBasketType().equals(basketType))
                .findAny()
                .orElse(null); //Baskets must always exists
    }

    public Set<BasketEntity> indexBasketsByUserId(Long userId) {
        userUtil.checkRightAccess(userId);
        return basketFinder.findBasketsByUserId(userId);
    }

    public BasketEntity getBasketById(Long id) {
        BasketEntity basketEntity = basketFinder.findBasketById(id);
        userUtil.checkRightAccess(basketEntity.getUser().getId());
        return basketEntity;
    }

    private void setCreationTimestamps(BasketEntity basketEntity) {
        Timestamp now = TimeUtil.now();
        basketEntity.setCreatedAt(now);
        basketEntity.setUpdatedAt(now);
    }
}
