package com.stanum.skrudzh.service.hash;

import com.stanum.skrudzh.jpa.model.HashEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.HashRepository;
import com.stanum.skrudzh.model.enums.HashableTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HashFinder {
    private final HashRepository hashRepository;

    public Optional<HashEntity> findHashByParams(UserEntity userEntity,
                                                 String saltEdgeCategory,
                                                 HashableTypeEnum hashableType,
                                                 String currency) {
        return hashRepository.findHashByParams(userEntity, saltEdgeCategory, hashableType, currency);
    }

    public Optional<HashEntity> findByUserEntityIdAAndPrototypeKey(Long id, String prototypeKey) {
        return hashRepository.findByUserIdAndPrototypeKey(id, prototypeKey);
    }

    public List<HashEntity> findAllByUserId(long id) {
        return hashRepository.findAllByUserId(id);
    }
}
