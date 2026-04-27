package org.envycorp.userservice.services;

import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.models.entity.User;
import org.envycorp.userservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "USER");
        user = new User();
        user.setId(USER_ID);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setHashedPassword("$2a$10$hashedPassword");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(USER_ID, null, List.of()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

     @Test
    void getUser_ExistingUser_Returns200WithBody() {
        UserResponseDto dto = new UserResponseDto("Test User", "test@example.com",
                user.getRole(), user.getCreatedAt());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        ResponseEntity<UserResponseDto> response = userService.getUser();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Test User");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void deleteUser_ExistingUser_DeletesAndReturnsVoid() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser();

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser())
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).delete(any());
    }

    @Test
    void updateUser_SameEmail_NewPassword_UpdatesPasswordAndReturns200() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("New Name", "test@example.com", "newPass");
        UserResponseDto responseDto = new UserResponseDto("New Name", "test@example.com",
                user.getRole(), user.getCreatedAt());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("newPass", user.getHashedPassword())).thenReturn(false);
        when(bCryptPasswordEncoder.encode("newPass")).thenReturn("newHashed");
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response = userService.updateUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(user.getHashedPassword()).isEqualTo("newHashed");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_SamePassword_DoesNotReHash() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("New Name", "test@example.com", "samePass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("samePass", user.getHashedPassword())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(dto);

        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUser_NewEmailNotTaken_ChangesEmail() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("Test User", "new@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(dto);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    void updateUser_NewEmailAlreadyTaken_ThrowsEmailIsAlreadyTakenException() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("Test User", "taken@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(dto))
                .isInstanceOf(EmailIsAlreadyTakenException.class)
                .hasMessage("Email is already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(
                new UserUpdateRequestDto("Name", "test@example.com", "pass")))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateUser_SameEmail_DoesNotCheckExistence() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("Test User", "test@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(dto);

        verify(userRepository, never()).existsByEmail(anyString());
    }
}
