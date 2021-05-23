package com.stanum.skrudzh.service.credit;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.CreditTypeEntity;
import com.stanum.skrudzh.jpa.repository.CreditTypeRepository;
import com.stanum.skrudzh.model.dto.CreditType;
import com.stanum.skrudzh.model.dto.CreditTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditTypesService {

    private final CreditTypeRepository creditTypeRepository;

    public Set<CreditTypeEntity> getAllCreditTypes() {
        return creditTypeRepository.findAllOrderByRowOrder();
    }

    public CreditTypes createCreditTypeResponse(Set<CreditTypeEntity> creditTypeEntities) {
        return new CreditTypes(creditTypeEntities.stream().map(CreditType::new).collect(Collectors.toList()));
    }

    public CreditTypeEntity getCreditTypeById(Long creditTypeId) {
        return creditTypeRepository.findById(creditTypeId).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

}
