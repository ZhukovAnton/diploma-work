package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.jpa.model.BasketEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.model.dto.Currency;
import com.stanum.skrudzh.model.dto.User;
import com.stanum.skrudzh.model.enums.BasketTypeEnum;
import com.stanum.skrudzh.service.basket.BasketFinder;
import com.stanum.skrudzh.service.basket.BasketService;
import com.stanum.skrudzh.service.currency.CurrencyService;
import com.stanum.skrudzh.service.transaction.TransactionFinder;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDtoService {

    private final BasketFinder basketFinder;

    private final BasketService basketService;

    private final TransactionFinder transactionFinder;

    public User createUserDto(UserEntity userEntity) {
        Set<BasketEntity> userBaskets = basketFinder.findBasketsByUserId(userEntity.getId());
        Currency currency = CurrencyService.getCurrencyByIsoCode(userEntity.getDefaultCurrency());
        Timestamp oldestTransactionGotAt = transactionFinder.findOldestTransactionGotAt(userEntity);
        if (!userEntity.getHasActiveSubscription() && oldestTransactionGotAt != null) {
            oldestTransactionGotAt = oldestTransactionGotAt.before(TimeUtil.beginningOfPreviousMonth())
                    ? TimeUtil.beginningOfPreviousMonth()
                    : oldestTransactionGotAt;
        }
        User userDto = new User(userEntity);
        userDto.setJoyBasketId(basketService.getBasketByType(BasketTypeEnum.joy, userBaskets).getId());
        userDto.setSafeBasketId(basketService.getBasketByType(BasketTypeEnum.safe, userBaskets).getId());
        userDto.setRiskBasketId(basketService.getBasketByType(BasketTypeEnum.risk, userBaskets).getId());
        userDto.setDefaultCurrency(currency);
        userDto.setOldestTransactionGotAt(oldestTransactionGotAt != null
                ? ZonedDateTime.of(oldestTransactionGotAt.toLocalDateTime(), ZoneId.of("Z"))
                : null);

        return userDto;
    }

}
