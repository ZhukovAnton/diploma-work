package com.stanum.skrudzh.service.basket;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.BasketRepository;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BasketFinder {

    private final BasketRepository basketRepository;

    public BasketEntity findBasketById(Long basketId) {
        return basketRepository.findById(basketId).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public Set<BasketEntity> findBasketsByUserId(Long userId) {
        return basketRepository.findBasketEntitiesByUser(userId);
    }

    public BasketEntity findBasketByUserAndType(UserEntity userEntity, BasketTypeEnum basketTypeEnum) {
        return basketRepository.findBasketEntityByUserAndBasketType(userEntity, basketTypeEnum).get(); //Baskets must always exists
    }

}
