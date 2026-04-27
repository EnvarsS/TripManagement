package org.envycorp.userservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.models.properties.JwtProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
public class JwtService {
    private final long expirationTime;
    private final SecretKey secretKey;

    public JwtService(JwtProperties props) {
        this.expirationTime = props.expirationTime();
        String configured = props.secret();

        if (configured == null || configured.isBlank()) {
            this.secretKey = Jwts.SIG.HS256.key().build();
            log.warn("No JWT secret configured — generated ephemeral key");
        } else {
            this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(configured));
        }
    }

    public String generateToken(User user) {
        String roleName = user.getRole().getName();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", roleName)
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseClaimsJws(token)
                .getPayload();

        return claims;
    }

    public Long extractUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return parseToken(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        return parseToken(token).getExpiration().after(new Date());
    }
}
