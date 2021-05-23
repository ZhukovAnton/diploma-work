package com.stanum.skrudzh.service.active;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.ActiveTypeEntity;
import com.stanum.skrudzh.jpa.repository.ActiveTypesRepository;
import com.stanum.skrudzh.model.dto.ActiveType;
import com.stanum.skrudzh.model.dto.ActiveTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActiveTypesService {

    private final ActiveTypesRepository activeTypesRepository;

    public List<ActiveTypeEntity> getAllActiveTypes() {
        return activeTypesRepository.findAllOrdered();
    }

    public ActiveTypeEntity getActiveTypeById(Long id) {
        return activeTypesRepository.findById(id).orElseThrow(() -> new AppException(HttpAppError.NOT_FOUND));
    }

    public ActiveTypes createActiveTypesResponse(List<ActiveTypeEntity> activeTypeEntities) {
        return new ActiveTypes(activeTypeEntities.stream().map(ActiveType::new).collect(Collectors.toList()));
    }

}
