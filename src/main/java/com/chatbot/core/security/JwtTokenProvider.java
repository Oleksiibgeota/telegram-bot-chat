package com.chatbot.core.security;

import com.chatbot.core.domain.User;
import com.chatbot.core.domain.UserSession;
import com.chatbot.core.dto.AuthorizedUserDTO;
import com.chatbot.core.exception.BadRequestException;
import com.chatbot.core.exception.JwtAuthenticationException;
import com.chatbot.core.exception.LoginUserException;
import com.chatbot.core.repositories.UserRepo;
import com.chatbot.core.repositories.UserSessionRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component

public class JwtTokenProvider {
    private final String accessTokenSecret;
    private final long accessValidityInMilliseconds;
    private final String refreshTokenSecret;
    private final long refreshValidityInMilliseconds;

    private final UserSessionRepo userSessionRepo;
    private final UserRepo userRepo;


    public JwtTokenProvider(@Value("${jwt.token.accessTokenSecret}") String accessTokenSecret,
                            @Value("${jwt.token.refreshTokenSecret}") String refreshTokenSecret,
                            @Value("${jwt.token.access-expired}") long accessValidityInMilliseconds,
                            @Value("${jwt.token.refresh-expired}") long refreshValidityInMilliseconds,
                            UserSessionRepo userSessionRepo,
                            UserRepo userRepo) {
        this.accessTokenSecret = Base64.getEncoder().encodeToString(accessTokenSecret.getBytes());
        this.refreshTokenSecret = Base64.getEncoder().encodeToString(refreshTokenSecret.getBytes());
        this.accessValidityInMilliseconds = accessValidityInMilliseconds;
        this.refreshValidityInMilliseconds = refreshValidityInMilliseconds;
        this.userSessionRepo = userSessionRepo;
        this.userRepo = userRepo;
    }

    public String generateToken(AuthorizedUserDTO userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return this.createToken(claims, userDetails);
    }

    public boolean isTokenValid(String token, AuthorizedUserDTO userDetails) {
        Claims claims = parseClaimFromAccessToken(token);
        return claims.getSubject().equals(userDetails.getEmail());
    }

    private String createToken(Map<String, Object> claims, AuthorizedUserDTO userDetails) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = this.getKey(this.accessTokenSecret, signatureAlgorithm.getJcaName());
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessValidityInMilliseconds);

        return "Bearer " + Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getEmail())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey, signatureAlgorithm)
                .compact();

    }

    public String createAccessToken(Claims claims, Set<String> authorities) {
        return createToken(claims, this.accessTokenSecret, this.accessValidityInMilliseconds, authorities);
    }

    public String createRefreshToken(Claims claims, Set<String> authorities) {
        return createToken(claims, this.refreshTokenSecret, this.refreshValidityInMilliseconds, authorities);
    }

    public Claims parseClaimFromAccessToken(String token) {
        return parseClaimFromToken(token, this.accessTokenSecret);
    }

    public Claims parseClaimFromRefreshToken(String token) {
        return parseClaimFromToken(token, this.refreshTokenSecret);
    }

    public AuthorizedUserDTO parseAccessToken(String accessToken) {
        Claims claim = this.parseClaimFromAccessToken(accessToken);
        return loadBySession(claim.getId());
    }

    private Claims parseClaimFromToken(String token, String secret) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = this.getKey(secret, signatureAlgorithm.getJcaName());
        try {
            return Jwts.parser()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JwtAuthenticationException("Unable to parse token");
        }
    }

    private String createToken(Claims claims, String secret, long validityInMilliseconds, Set<String> authorities) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = this.getKey(secret, signatureAlgorithm.getJcaName());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return "Bearer " + Jwts.builder()
                .setClaims(claims)
                .claim("authorities", authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(signingKey, signatureAlgorithm)
                .compact();
    }

    private Key getKey(String secret, String jcaName) {
        byte[] apiKeySecretBytes = secret.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(apiKeySecretBytes, jcaName);
    }

    private AuthorizedUserDTO loadBySession(String sessionId) {
        log.debug("SessionToUserCache.loadBySession:" + sessionId);
        UserSession maybeSession = userSessionRepo.getUserBySessionUuid(sessionId).orElseThrow(
                () -> new LoginUserException("Session does not exist or expired"));

        LocalDateTime expiredAt = maybeSession.getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new LoginUserException("Session is expired");
        }

        Long userId = maybeSession.getUserId();
        User user = userRepo.getById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        return new AuthorizedUserDTO(user.getId(), user.getUuid(), user.getEmail(),  user.getAuthorities());
    }
}
