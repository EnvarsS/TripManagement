package org.envycorp.userservice.controller;

import org.envycorp.commonmodule.dto.request.users.UserCreateRequestDto;
import org.envycorp.commonmodule.dto.request.users.UserUpdateRequestDto;
import org.envycorp.commonmodule.dto.response.users.RoleResponseDto;
import org.envycorp.commonmodule.dto.response.users.UserResponseDto;
import org.envycorp.userservice.config.SecurityConfig;
import org.envycorp.userservice.controllers.UserController;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.filter.JwtFilter;
import org.envycorp.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean  private UserService userService;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto(
                1L, "Test User", "test@example.com",
                new RoleResponseDto(1L, "USER"),
                LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUser_AuthenticatedUser_Returns200WithBody() throws Exception {
        when(userService.getUser(1L)).thenReturn(ResponseEntity.ok(userResponseDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUser_UserNotFound_Returns400() throws Exception {
        when(userService.getUser(1L))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_Admin_Returns201() throws Exception {
        UserCreateRequestDto dto = new UserCreateRequestDto(
                "new@example.com", "password123", "New User");

        when(userService.createUser(any()))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto));

        mockMvc.perform(post("/users").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_BlankEmail_Returns400() throws Exception {
        mockMvc.perform(post("/users").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("", "password123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_DuplicateEmail_Returns400() throws Exception {
        when(userService.createUser(any()))
                .thenThrow(new EmailIsAlreadyTakenException("Email Already Exists"));

        mockMvc.perform(post("/users").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("taken@example.com", "password123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email Already Exists"));
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void updateUser_Owner_Returns200() throws Exception {
        UserResponseDto updated = new UserResponseDto(
                1L, "New Name", "new@example.com",
                new RoleResponseDto(1L, "USER"), LocalDateTime.now());

        when(userService.updateUser(eq(1L), any())).thenReturn(ResponseEntity.ok(updated));

        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("New Name", "new@example.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_Admin_CanUpdateAnyUser_Returns200() throws Exception {
        when(userService.updateUser(eq(99L), any())).thenReturn(ResponseEntity.ok(userResponseDto));

        mockMvc.perform(put("/users/99").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("New Name", "new@example.com", "password123"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_EmailAlreadyTaken_Returns400() throws Exception {
        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new EmailIsAlreadyTakenException("Email is already taken"));

        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "taken@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already taken"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_UserNotFound_Returns400() throws Exception {
        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "test@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_BlankName_Returns400() throws Exception {
        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("", "test@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_InvalidEmail_Returns400() throws Exception {
        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "not-an-email", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isArray());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_BlankPassword_Returns400() throws Exception {
        mockMvc.perform(put("/users/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "test@example.com", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isArray());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deleteUser_Owner_Returns204() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_Admin_CanDeleteAnyUser_Returns204() throws Exception {
        doNothing().when(userService).deleteUser(99L);

        mockMvc.perform(delete("/users/99").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(99L);
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void deleteUser_UserNotFound_Returns400() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }
}