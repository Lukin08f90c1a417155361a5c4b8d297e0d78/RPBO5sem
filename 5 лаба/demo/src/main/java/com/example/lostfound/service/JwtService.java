package com.example.lostfound.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String SECRET = "mySuperSecretKeyForJwtTokensWhichIsLongEnoughForHS256Algorithm";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    private final long ACCESS_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 15 минут
    private final long REFRESH_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 дней

    public String generateAccessToken(String username) {
        return generateToken(username, "access", ACCESS_EXPIRATION_MS);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, "refresh", REFRESH_EXPIRATION_MS);
    }

    private String generateToken(String username, String type, long expirationMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", type);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedType) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return expectedType.equals(type) && claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getAccessExpirationMs() { return ACCESS_EXPIRATION_MS; }
    public long getRefreshExpirationMs() { return REFRESH_EXPIRATION_MS; }
}