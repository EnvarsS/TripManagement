package org.envycorp.userservice.services;

import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.RoleResponseDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private ModelMapper modelMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;
    private RoleResponseDto roleResponseDto;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "USER");
        roleResponseDto = new RoleResponseDto(1L, "USER");

        user = new User();
        user.setId(USER_ID);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setHashedPassword("$2a$10$hashedPassword");
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getUser_ExistingUser_Returns200WithBody() {
        UserResponseDto dto = new UserResponseDto(
                USER_ID, "Test User", "test@example.com", roleResponseDto, user.getCreatedAt());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(dto);

        ResponseEntity<UserResponseDto> response = userService.getUser(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(USER_ID);
        assertThat(response.getBody().getName()).isEqualTo("Test User");
        assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void createUser_NewEmail_SavesUserAndReturns201() {
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "new@example.com", "password123", "New User");
        User mappedUser = new User();
        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setRole(role);
        UserResponseDto responseDto = new UserResponseDto(
                2L, "New User", "new@example.com", roleResponseDto, LocalDateTime.now());

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(roleRepository.findByName("USER")).thenReturn(role);
        when(bCryptPasswordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserResponseDto.class)).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response = userService.createUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(mappedUser.getRole()).isEqualTo(role);
        assertThat(mappedUser.getHashedPassword()).isEqualTo("encoded");
        verify(userRepository).save(mappedUser);
    }

    @Test
    void createUser_DuplicateEmail_ThrowsEmailIsAlreadyTakenException() {
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "test@example.com", "password123", "Test");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(EmailIsAlreadyTakenException.class)
                .hasMessage("Email Already Exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_RoleAssignedBeforeSave() {
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "new@example.com", "password123", "User");
        User mappedUser = new User();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(roleRepository.findByName("USER")).thenReturn(role);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hash");
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.createUser(dto);

        assertThat(mappedUser.getRole()).isEqualTo(role);
        verify(roleRepository).findByName("USER");
    }

    @Test
    void createUser_PasswordEncodedBeforeSave() {
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "new@example.com", "plaintext", "User");
        User mappedUser = new User();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(roleRepository.findByName("USER")).thenReturn(role);
        when(bCryptPasswordEncoder.encode("plaintext")).thenReturn("$2a$hashed");
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.createUser(dto);

        assertThat(mappedUser.getHashedPassword()).isEqualTo("$2a$hashed");
        verify(bCryptPasswordEncoder).encode("plaintext");
    }

    @Test
    void updateUser_SameEmail_NewPassword_UpdatesAndReturns200() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "New Name", "test@example.com", "newPass");
        UserResponseDto responseDto = new UserResponseDto(
                USER_ID, "New Name", "test@example.com", roleResponseDto, user.getCreatedAt());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("newPass", user.getHashedPassword())).thenReturn(false);
        when(bCryptPasswordEncoder.encode("newPass")).thenReturn("newHashed");
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        ResponseEntity<UserResponseDto> response = userService.updateUser(USER_ID, dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(user.getHashedPassword()).isEqualTo("newHashed");
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_SamePassword_DoesNotReHash() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "New Name", "test@example.com", "samePass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches("samePass", user.getHashedPassword())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(USER_ID, dto);

        verify(bCryptPasswordEncoder, never()).encode(anyString());
    }

    @Test
    void updateUser_NewEmailNotTaken_ChangesEmail() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "Test User", "new@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(USER_ID, dto);

        assertThat(user.getEmail()).isEqualTo("new@example.com");
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    void updateUser_NewEmailAlreadyTaken_ThrowsEmailIsAlreadyTakenException() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "Test User", "taken@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(USER_ID, dto))
                .isInstanceOf(EmailIsAlreadyTakenException.class)
                .hasMessage("Email is already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(USER_ID,
                new UserUpdateRequestDto("Name", "test@example.com", "pass")))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void updateUser_SameEmail_DoesNotCheckExistence() {
        UserUpdateRequestDto dto = new UserUpdateRequestDto(
                "Test User", "test@example.com", "pass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(new UserResponseDto());

        userService.updateUser(USER_ID, dto);

        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    void deleteUser_ExistingUser_DeletesSuccessfully() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.deleteUser(USER_ID);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, never()).delete(any());
    }
}