package com.example.JWT.service;

import com.example.JWT.entity.Role;
import com.example.JWT.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JWTService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    // default 10 minutes in millis, can override in application.properties
    @Value("${jwt.expirationMillis:600000}")
    private long expirationMillis;

    private SecretKey secretKey;

    @PostConstruct
    private void init() {
        // validate secret length - Keys.hmacShaKeyFor requires sufficiently long key bytes
        if (jwtSecretKey == null || jwtSecretKey.trim().length() < 32) {
            throw new IllegalStateException("jwt.secretKey must be set and at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        // setting expiration date for token
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(secretKey) // Keys.hmacShaKeyFor produced a valid HMAC key
                .compact();
    }

    /**
     * Parses the token and returns the Claims if token is valid (signature + structure).
     * Throws JwtException on invalid token (expired, malformed, signature invalid).
     */
    private Claims parseClaims(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token); // throws JwtException on problems
        return jws.getBody();
    }

    /**
     * Returns userId (subject) from token. Throws JwtException if token invalid/expired.
     */
    public Long getUserIdFromToken(String token) throws JwtException {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        return subject == null ? null : Long.valueOf(subject);
    }

    /**
     * Returns roles that were embedded into the token as List<String>.
     * Throws JwtException if token invalid.
     */
    public List<String> getRolesFromToken(String token) throws JwtException {
        Claims claims = parseClaims(token);
        Object raw = claims.get("roles");
        if (raw instanceof List<?>) {
            return ((List<?>) raw).stream()
                    .filter(o -> o != null)
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Simple token validity check (signature + expiration).
     * Returns true if token is correctly signed and not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            Date exp = claims.getExpiration();
            return exp == null || exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
