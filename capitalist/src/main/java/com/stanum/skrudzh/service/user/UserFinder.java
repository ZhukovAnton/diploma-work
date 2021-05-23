package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.converters.Encryptor;
import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFinder {

    private final UserRepository userRepository;

    private final Encryptor encryptor;

    public Optional<UserEntity> findByEmailConfirmationCode(String code) {
        return userRepository.findByEmailConfirmationCode(code);
    }

    public UserEntity findUserByEmail(String email) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(encryptor.convertToDatabaseColumn(email));
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new AppException(HttpAppError.UNAUTHORIZED);
        }
    }

    public List<UserEntity> findAllUsersWithoutSaltEdge() {
        return userRepository.findAllWithoutSaltEdge();
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
}
