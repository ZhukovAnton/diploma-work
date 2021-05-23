package com.stanum.skrudzh.service.saltedge.connection;

import com.stanum.skrudzh.jpa.model.ConnectionEntity;
import com.stanum.skrudzh.model.dto.ConnectionDto;
import com.stanum.skrudzh.saltage.model.Connection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionDtoService {

    public <T> List<ConnectionDto> createConnectionsDto(Set<T> connectionEntities) {
        return connectionEntities.stream()
                .map(this::createConnectionDto)
                .collect(Collectors.toList());
    }

    public ConnectionDto createConnectionDto(Object connectionEntity) {
        if (connectionEntity instanceof ConnectionEntity) {
            return new ConnectionDto((ConnectionEntity) connectionEntity);
        } else {
            return new ConnectionDto((Connection) connectionEntity);
        }
    }

}
