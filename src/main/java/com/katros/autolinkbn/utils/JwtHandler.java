package com.katros.autolinkbn.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Component
public class JwtHandler {

    private final SecretKey key;
    private final SecretKey resetPasswordKey;
    private final long jwtExpirationInMs;

    public JwtHandler(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.reset-password-secret}") String resetPasswordSecret,
                      @Value("${jwt.expiration}") long jwtExpirationInMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.resetPasswordKey = Keys.hmacShaKeyFor(resetPasswordSecret.getBytes());
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(String userId, String email, List<String> roles, boolean verified) {
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtExpirationInMs, ChronoUnit.MILLIS);

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("roles", roles)
                .claim("verified", verified)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }


    public String generateResetPasswordToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3 * 60 * 1000))
                .signWith(resetPasswordKey)
                .compact();
    }

    public String extractEmail(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractUserId(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        return parser.parseClaimsJws(token)
                .getBody()
                .get("userId").toString();
    }

    public String extractRole(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        return parser.parseClaimsJws(token)
                .getBody()
                .get("role").toString();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("roles", List.class);
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build();

            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmailResetToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(resetPasswordKey)
                .build();

        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();

    }

    public boolean validateResetPasswordToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(resetPasswordKey)
                    .build();

            parser.parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


}

