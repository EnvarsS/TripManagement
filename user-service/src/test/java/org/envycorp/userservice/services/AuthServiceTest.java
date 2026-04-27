package org.envycorp.userservice.services;

import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.IncorrectEmailException;
import org.envycorp.userservice.exceptions.IncorrectPasswordException;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.RoleRepository;
import org.envycorp.userservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role(1L, "USER");
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setHashedPassword("$2a$10$hashedPassword");
        user.setRole(userRole);
    }

    @Test
    void login_ValidCredentials_Returns200WithToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("password123", user.getHashedPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<String> response = authService.login(
                new UserLoginRequestDto("test@example.com", "password123"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("jwt-token");
    }

    @Test
    void login_UnknownEmail_ThrowsIncorrectEmailException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                new UserLoginRequestDto("unknown@example.com", "password123")))
                .isInstanceOf(IncorrectEmailException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_WrongPassword_ThrowsIncorrectPasswordException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("wrongpass", user.getHashedPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                new UserLoginRequestDto("test@example.com", "wrongpass")))
                .isInstanceOf(IncorrectPasswordException.class)
                .hasMessage("Invalid password");
    }

    @Test
    void login_DoesNotCallJwtService_WhenPasswordWrong() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(
                new UserLoginRequestDto("test@example.com", "bad")));

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void createUser_NewEmail_SavesAndReturnsToken() {
        UserCreateRequestDto dto = new UserCreateRequestDto("new@example.com", "password123", "New User");
        User mappedUser = new User();

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encoded");
        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("new-token");

        ResponseEntity<String> response = authService.createUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("new-token");
        assertThat(mappedUser.getHashedPassword()).isEqualTo("encoded");
        assertThat(mappedUser.getRole()).isEqualTo(userRole);
    }

    @Test
    void createUser_DuplicateEmail_ThrowsEmailIsAlreadyTakenException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.createUser(
                new UserCreateRequestDto("test@example.com", "password123", "Test")))
                .isInstanceOf(EmailIsAlreadyTakenException.class)
                .hasMessage("Email Already Exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_AlwaysAssignsDefaultUserRole() {
        UserCreateRequestDto dto = new UserCreateRequestDto("new@example.com", "password123", "User");
        User mappedUser = new User();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hash");
        when(roleRepository.findByName("USER")).thenReturn(userRole);
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("token");

        authService.createUser(dto);

        verify(roleRepository).findByName("USER");
        assertThat(mappedUser.getRole()).isEqualTo(userRole);
    }

    @Test
    void createUser_PasswordIsEncoded_BeforeSaving() {
        UserCreateRequestDto dto = new UserCreateRequestDto("new@example.com", "plaintext", "User");
        User mappedUser = new User();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(bCryptPasswordEncoder.encode("plaintext")).thenReturn("$2a$hashed");
        when(roleRepository.findByName(anyString())).thenReturn(userRole);
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn("token");

        authService.createUser(dto);

        assertThat(mappedUser.getHashedPassword()).isEqualTo("$2a$hashed");
        verify(bCryptPasswordEncoder).encode("plaintext");
    }
}
