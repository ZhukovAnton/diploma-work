package com.stanum.skrudzh.service.borrow;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.BorrowEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.BorrowRepository;
import com.stanum.skrudzh.model.enums.BorrowTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowFinder {

    private final BorrowRepository borrowRepository;

    public BorrowEntity findBorrowByIdAndType(Long id, BorrowTypeEnum borrowType) {
        return borrowRepository.findByIdAndType(id, borrowType).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public BorrowEntity findById(Long id) {
        return borrowRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND, "not found"));
    }

    public Set<BorrowEntity> findNotReturnedBorrows(UserEntity userEntity, BorrowTypeEnum borrowType) {
        return borrowRepository.getNotReturnedUserBorrows(userEntity, borrowType);
    }

    public Set<BorrowEntity> findAllByUserAndType(UserEntity userEntity, BorrowTypeEnum borrowType) {
        return borrowRepository.findAllByUserAndType(userEntity, borrowType);
    }

    public Set<BorrowEntity> findAllByUserEntity(UserEntity userEntity) {
        return borrowRepository.findAllByUser(userEntity);
    }

    public Set<String> findAllBorrowsCurrencies(UserEntity userEntity) {
        return borrowRepository.findAllBorrowsCurrencies(userEntity);
    }
}
