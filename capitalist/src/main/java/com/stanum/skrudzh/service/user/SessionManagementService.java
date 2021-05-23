package com.stanum.skrudzh.service.user;

import com.stanum.skrudzh.exception.AppException;
import com.stanum.skrudzh.exception.HttpAppError;
import com.stanum.skrudzh.jpa.model.SessionEntity;
import com.stanum.skrudzh.jpa.model.UserEntity;
import com.stanum.skrudzh.jpa.repository.SessionRepository;
import com.stanum.skrudzh.jpa.repository.UserRepository;
import com.stanum.skrudzh.metrics.Metric;
import com.stanum.skrudzh.metrics.MetricType;
import com.stanum.skrudzh.metrics.MetricsService;
import com.stanum.skrudzh.utils.CodeGenerator;
import com.stanum.skrudzh.utils.RequestUtil;
import com.stanum.skrudzh.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionManagementService {

    private final SessionRepository sessionRepository;

    private final UserRepository userRepository;

    private final MetricsService metricsService;

    public void deleteSession(SessionEntity session) {
        UserEntity currentUser = RequestUtil.getUser();
        if (userRepository.existsById(currentUser.getId()) && session.getUser().equals(currentUser)) {
            sessionRepository.delete(session);
        } else {
            throw new AppException(HttpAppError.UNAUTHORIZED);
        }
    }

    public SessionEntity getByToken(String token) {
        Optional<SessionEntity> optionalSessionEntity = sessionRepository.findByToken(token);
        if (optionalSessionEntity.isPresent()) {
            return optionalSessionEntity.get();
        } else {
            throw new AppException(HttpAppError.NOT_FOUND);
        }
    }

    private boolean isExist(String token) {
        return sessionRepository.findByToken(token).isPresent();
    }

    public SessionEntity createSession(UserEntity userEntity) {
        Timestamp now = TimeUtil.now();
        String token = CodeGenerator.generateToken();
        while (isExist(token)) {
            log.info("Token {} already exist, generate new one", token);
            token = CodeGenerator.generateToken();
        }
        metricsService.saveMetric(MetricType.GENERATE_TOKEN, now.getTime());
        return new SessionEntity(token, userEntity, now, now);
    }

    public void save(SessionEntity sessionEntity) {
        sessionRepository.save(sessionEntity);
    }
}
