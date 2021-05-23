package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.CreditEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CreditFinder {

    private final CreditRepository creditRepository;

    public CreditEntity findByIdWithNull(Long id) {
        return creditRepository.findCreditByIdWithNull(id);
    }

    public CreditEntity findById(Long id) {
        return creditRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "not found"));
    }

    public Set<CreditEntity> findUnpaidCredits(UserEntity userEntity) {
        return creditRepository.getUnpaidCredits(userEntity);
    }

    public Set<CreditEntity> findAllByUser(UserEntity userEntity) {
        return creditRepository.findAllByUserEntity(userEntity);
    }

    public Set<String> findAllCreditsCurrencies(UserEntity userEntity) {
        return creditRepository.findAllCreditsCurrencies(userEntity);
    }

}
