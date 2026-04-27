package org.envycorp.userservice.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.envycorp.userservice.filter.JwtFilter;
import org.envycorp.userservice.services.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthHeader_PassesChainWithoutSettingAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void basicScheme_PassesChainWithoutSettingAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(jwtService);
    }

    @Test
    void expiredToken_PassesChainWithoutSettingAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
        when(jwtService.extractUserId("expired.jwt.token"))
                .thenThrow(mock(ExpiredJwtException.class));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void expiredToken_DoesNotCallExtractRole() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.jwt.token");
        when(jwtService.extractUserId("expired.jwt.token"))
                .thenThrow(mock(ExpiredJwtException.class));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractRole(anyString());
    }

    @Test
    void tamperedToken_PassesChainWithoutSettingAuth() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tampered.jwt.token");
        when(jwtService.extractUserId("tampered.jwt.token"))
                .thenThrow(new JwtException("Invalid signature"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void tamperedToken_DoesNotCallExtractRole() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tampered.jwt.token");
        when(jwtService.extractUserId("tampered.jwt.token"))
                .thenThrow(new JwtException("Invalid signature"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractRole(anyString());
    }

    @Test
    void validToken_SetsAuthenticationInSecurityContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUserId("valid.jwt.token")).thenReturn(42L);
        when(jwtService.extractRole("valid.jwt.token")).thenReturn("USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(42L);
    }

    @Test
    void validToken_SetsCorrectUserRole() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUserId("valid.jwt.token")).thenReturn(1L);
        when(jwtService.extractRole("valid.jwt.token")).thenReturn("USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void validToken_SetsCorrectAdminRole() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer admin.jwt.token");
        when(jwtService.extractUserId("admin.jwt.token")).thenReturn(1L);
        when(jwtService.extractRole("admin.jwt.token")).thenReturn("ADMIN");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void validToken_AlwaysContinuesFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUserId("valid.jwt.token")).thenReturn(1L);
        when(jwtService.extractRole("valid.jwt.token")).thenReturn("USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void validToken_AuthAlreadySet_DoesNotOverwriteExistingAuth() throws Exception {
        var existing = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken(99L, null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);

        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUserId("valid.jwt.token")).thenReturn(42L);
        when(jwtService.extractRole("valid.jwt.token")).thenReturn("USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isEqualTo(99L);
        verify(filterChain).doFilter(request, response);
    }
}
