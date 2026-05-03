package org.envycorp.userservice.services;

import io.jsonwebtoken.ExpiredJwtException;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.models.properties.JwtProperties;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {
    private static final String VALID_SECRET = "zparg/U78AiX/gVUrPSryQLT/BCJA+apoj6NlpCadRw=";

    private JwtService jwtServiceWithSecret;
    private JwtService jwtServiceEphemeral;
    private User user;

    @BeforeEach
    void setUp() {
        jwtServiceWithSecret = new JwtService(new JwtProperties(VALID_SECRET, 3600000L));
        jwtServiceEphemeral = new JwtService(new JwtProperties("", 3600000L));

        Role role = new Role(1L, "USER");
        user = new User();
        user.setId(42L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setHashedPassword("hashed");
        user.setRole(role);
    }

    @Test
    void generateToken_ReturnsNonNullToken() {
        assertThat(jwtServiceWithSecret.generateToken(user)).isNotNull().isNotBlank();
    }

    @Test
    void generateToken_HasThreeJwtParts() {
        assertThat(jwtServiceWithSecret.generateToken(user).split("\\.")).hasSize(3);
    }

    @Test
    void generateToken_EphemeralKey_ReturnsValidToken() {
        String token = jwtServiceEphemeral.generateToken(user);
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractUserId_ReturnsCorrectId() {
        String token = jwtServiceWithSecret.generateToken(user);
        assertThat(jwtServiceWithSecret.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    void extractRole_UserRole_ReturnsUser() {
        String token = jwtServiceWithSecret.generateToken(user);
        assertThat(jwtServiceWithSecret.extractRole(token)).isEqualTo("USER");
    }

    @Test
    void extractRole_AdminRole_ReturnsAdmin() {
        user.setRole(new Role(2L, "ADMIN"));
        String token = jwtServiceWithSecret.generateToken(user);
        assertThat(jwtServiceWithSecret.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void parseToken_SubjectIsUserEmail() {
        String token = jwtServiceWithSecret.generateToken(user);
        assertThat(jwtServiceWithSecret.parseToken(token).getSubject())
                .isEqualTo("test@example.com");
    }
}
