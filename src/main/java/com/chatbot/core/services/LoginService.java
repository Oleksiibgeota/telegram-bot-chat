package com.chatbot.core.services;

import com.chatbot.core.domain.User;
import com.chatbot.core.domain.UserSession;
import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.dto.LoginRequestDTO;
import com.chatbot.core.exception.LoginUserException;
import com.chatbot.core.repositories.UserRepo;
import com.chatbot.core.repositories.UserSessionRepo;
import com.chatbot.core.security.JwtTokenProvider;
import com.chatbot.core.utils.PasswordResolver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

    private @Value("${user.session.measurement}")
    String sessionMeasurement;
    private @Value("${user.session.ttl-size}")
    Integer sessionTtlSize;
    private final UserRepo userRepo;
    private final UserSessionRepo userSessionRepo;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthorizedUserDTO login(LoginRequestDTO loginRequest, String ip) {
        User user = userRepo.findByMail(loginRequest.getEmail())
                .orElseThrow(() -> new LoginUserException(
                        "Current user is not signed up or doesn't exist"));
        if (!user.isActive()) {
            throw new LoginUserException("User is not activated");
        }
        if (!PasswordResolver.matches(user.getPassword(), loginRequest.getPassword())) {
            throw new LoginUserException("Password is incorrect");
        }
        return createSessionForUserId(user, ip);
    }

    private AuthorizedUserDTO createSessionForUserId(User user, String ip) {
        Claims claims = Jwts.claims()
                .setSubject(user.getEmail())
                .setId(UUID.randomUUID().toString())
                .setIssuer(user.getUuid())
                .build();

        String accessToken = this.jwtTokenProvider.createAccessToken(claims, user.getAuthorities());
        String refreshToken = this.jwtTokenProvider.createRefreshToken(claims, user.getAuthorities());
        createSession(user.getId(), claims.getId(), ip);
        return new AuthorizedUserDTO(accessToken, refreshToken);
    }

    private void createSession(Long userId, String sessionId, String ip) {
        LocalDateTime createAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneOffset.UTC);
        LocalDateTime expiredAt = createAt.plus(sessionTtlSize, ChronoUnit.valueOf(sessionMeasurement));
        UserSession userSession = UserSession.builder()
                .userId(userId)
                .sessionUuid(sessionId)
                .createdAt(createAt)
                .expiredAt(expiredAt)
                .ip(ip)
                .build();
        this.userSessionRepo.create(userSession);
    }
}
