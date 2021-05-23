package com.stanum.skrudzh.service.hash;

import com.stanum.skrudzh.jpa.model.HashEntity;
import com.stanum.skrudzh.jpa.model.HashExampleEntity;
import com.stanum.skrudzh.jpa.model.base.Hashable;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.HashExampleRepository;
import com.stanum.skrudzh.jpa.repository.HashRepository;
import com.stanum.skrudzh.service.hashable.HashableFinder;
import com.stanum.skrudzh.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashManagementService {

    private final HashFinder hashFinder;

    private final UserFinder userFinder;

    private final HashableFinder hashableFinder;

    private final HashRepository hashRepository;

    private final HashExampleRepository hashExampleRepository;

    public void createHash(Hashable hashable, HashExampleEntity hashExampleEntity, UserEntity userEntity) {
        log.info("Create hash for user id={}, hashExample={}, hashableId={}", userEntity.getId(), hashExampleEntity, hashable.getId());
        HashEntity newHash = createHash(hashExampleEntity, userEntity);
        setHashableData(newHash, hashable);
        save(newHash);
    }

    public HashEntity createHash(HashExampleEntity hashExampleEntity, UserEntity userEntity) {
        log.info("Create hash for user id={}, hashableEntity={}", userEntity.getId(), hashExampleEntity);
        HashEntity hashEntity = new HashEntity();
        hashEntity.setSaltEdgeCategory(hashExampleEntity.getSaltEdgeCategories());
        hashEntity.setUser(userEntity);
        hashEntity.setHashableType(hashExampleEntity.getHashableType());
        hashEntity.setPrototypeKey(hashExampleEntity.getPrototypeKey());
        return save(hashEntity);
    }

    public void createUserHash(Hashable hashable, String saltEdgeCategories, UserEntity userEntity) {
        HashEntity hashEntity = new HashEntity();
        hashEntity.setSaltEdgeCategory(saltEdgeCategories);
        hashEntity.setUser(userEntity);
        setHashableData(hashEntity, hashable);
        save(hashEntity);
    }

    public void updateHashWithHashable(HashEntity hashEntity, Hashable hashable) {
        hashEntity.setHashableId(hashable.getId());
        hashEntity.setHashableType(hashable.getHashableType());
        hashEntity.setHashableCurrency(hashable.getCurrency());
        save(hashEntity);
    }

    public void createHashesForUsers() {
        List<HashExampleEntity> hashExamples = hashExampleRepository.findAll();
        userFinder.findAll().forEach(user -> {
            Set<String> prototypeKeys = hashFinder.findAllByUserId(user.getId()).stream().map(HashEntity::getPrototypeKey).collect(Collectors.toSet());
            if (prototypeKeys.size() != 28) {
                hashExamples.stream()
                        .filter(hashExample -> !prototypeKeys.contains(hashExample.getPrototypeKey()))
                        .forEach(hashExample -> {
                            Optional<Hashable> hashable = hashableFinder.findHashable(hashExample.getHashableType(), hashExample.getPrototypeKey(), user);
                            hashable.ifPresentOrElse(hashableEntity -> createHash(hashableEntity, hashExample, user), () -> createHash(hashExample, user));
                        });
            }
        });
    }

    private void setHashableData(HashEntity hashEntity, Hashable hashable) {
        hashEntity.setHashableType(hashable.getHashableType());
        hashEntity.setHashableId(hashable.getId());
        hashEntity.setHashableCurrency(hashable.getCurrency());
    }

    private HashEntity save(HashEntity hashEntity) {
        return hashRepository.save(hashEntity);
    }
}
