package org.envycorp.userservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.envycorp.userservice.models.entity.User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {
    private final long EXPIRATION_TIME = 3600 * 1000;
    private final String SECRET_PHRASE = "zparg/U78AiX/gVUrPSryQLT/BCJA+apoj6NlpCadRw=";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_PHRASE));

    public String generateToken(User user) {
        String roleName = user.getRole().getName();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", roleName)
                .claim("userId", user.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Claims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getPayload();
        if (claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Token is expired");
        }

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
