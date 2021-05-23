package com.stanum.skrudzh.service.basket;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.model.dto.Basket;
import com.stanum.skrudzh.model.dto.Baskets;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasketDtoService {

    private final CurrencyService currencyService;

    private final BasketCalculationService calculationService;

    public Baskets createBasketsResponse(Set<BasketEntity> basketEntities) {
        return new Baskets(basketEntities
                .stream()
                .map(this::createBasketResponse)
                .collect(Collectors.toList()));
    }

    public Basket createBasketResponse(BasketEntity basketEntity) {
        Basket basket = new Basket(basketEntity);
        Currency currency = currencyService
                .getCurrencyByIsoCode(basketEntity
                        .getUser()
                        .getDefaultCurrency());
        basket.setCurrency(currency);
        basket.setSpentCurrency(currency.getIsoCode());
        basket.setSpentCentsAtPeriod(calculationService
                .getSpentAtPeriod(basketEntity, basketEntity.getUser().getDefaultPeriod(), false).longValue());
        return basket;
    }

}
