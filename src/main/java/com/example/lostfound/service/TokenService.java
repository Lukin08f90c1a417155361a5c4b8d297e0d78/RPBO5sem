package com.example.lostfound.service;

import java.time.Instant;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lostfound.model.SessionStatus;
import com.example.lostfound.model.User;
import com.example.lostfound.model.UserSession;
import com.example.lostfound.repository.UserRepository;
import com.example.lostfound.repository.UserSessionRepository;

@Service
public class TokenService {

    private final JwtService jwtService;
    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;

    public TokenService(JwtService jwtService, UserSessionRepository sessionRepository, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, String> issueTokens(String username, String deviceId) {
        String access = jwtService.generateAccessToken(username);
        String refresh = jwtService.generateRefreshToken(username);

        UserSession session = new UserSession();
        session.setUsername(username);
        session.setDeviceId(deviceId);
        session.setAccessToken(access);
        session.setRefreshToken(refresh);
        session.setAccessTokenExpiry(Instant.now().plusMillis(jwtService.getAccessExpirationMs()));
        session.setRefreshTokenExpiry(Instant.now().plusMillis(jwtService.getRefreshExpirationMs()));
        session.setStatus(SessionStatus.ACTIVE);
        
        sessionRepository.save(session);
        return Map.of("accessToken", access, "refreshToken", refresh);
    }

    // Используем noRollbackFor, чтобы сохранить статус REVOKED при ошибке
    @Transactional(noRollbackFor = { RuntimeException.class })
    public Map<String, String> refreshTokens(String refreshToken, String deviceId) {
        if (!jwtService.isTokenValid(refreshToken, "refresh")) {
            throw new RuntimeException("Неверный или просроченный refresh токен");
        }

        UserSession session = sessionRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Сессия не найдена"));

        // Проверка на компрометацию (Reuse detection)
        if (session.getStatus() == SessionStatus.USED) {
            session.setStatus(SessionStatus.REVOKED);
            sessionRepository.saveAndFlush(session); // Принудительно сохраняем отзыв
            throw new RuntimeException("Токен уже был использован. Сессия отозвана.");
        }

        if (session.getStatus() == SessionStatus.REVOKED) {
            throw new RuntimeException("Сессия отозвана");
        }

        if (session.getRefreshTokenExpiry().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh токен истек");
        }

        User user = userRepository.findByUsername(session.getUsername())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (!user.isEnabled()) {
            throw new RuntimeException("Пользователь заблокирован");
        }

        // Помечаем старую сессию как использованную
        session.setStatus(SessionStatus.USED);
        sessionRepository.save(session);

        // Выдаем новую пару (метод issueTokens создаст новую запись ACTIVE)
        return issueTokens(session.getUsername(), deviceId != null ? deviceId : session.getDeviceId());
    }
}