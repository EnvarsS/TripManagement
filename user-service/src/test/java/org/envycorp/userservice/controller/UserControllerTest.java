package org.envycorp.userservice.controller;

import org.envycorp.userservice.controllers.UserController;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.UserNotFoundException;
import org.envycorp.userservice.filter.JwtFilter;
import org.envycorp.userservice.models.dto.request.UserUpdateRequestDto;
import org.envycorp.userservice.models.dto.response.UserResponseDto;
import org.envycorp.userservice.models.entity.Role;
import org.envycorp.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userResponseDto = new UserResponseDto(
                "Test User", "test@example.com",
                new Role(1L, "USER"), LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void getMe_AuthenticatedUser_Returns200WithBody() throws Exception {
        when(userService.getUser()).thenReturn(ResponseEntity.ok(userResponseDto));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    void getMe_UserNotFound_Returns400() throws Exception {
        when(userService.getUser())
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser
    void updateMe_ValidRequest_Returns200WithUpdatedBody() throws Exception {
        UserUpdateRequestDto dto = new UserUpdateRequestDto("New Name", "new@example.com", "password123");
        UserResponseDto updated = new UserResponseDto("New Name", "new@example.com",
                new Role(1L, "USER"), LocalDateTime.now());

        when(userService.updateUser(any())).thenReturn(ResponseEntity.ok(updated));

        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @WithMockUser
    void updateMe_EmailAlreadyTaken_Returns400() throws Exception {
        when(userService.updateUser(any()))
                .thenThrow(new EmailIsAlreadyTakenException("Email is already taken"));

        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "taken@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already taken"));
    }

    @Test
    @WithMockUser
    void updateMe_UserNotFound_Returns400() throws Exception {
        when(userService.updateUser(any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "test@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    @Test
    @WithMockUser
    void updateMe_BlankName_Returns400WithFieldError() throws Exception {
        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("", "test@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isArray());
    }

    @Test
    @WithMockUser
    void updateMe_InvalidEmail_Returns400WithFieldError() throws Exception {
        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "not-an-email", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isArray());
    }

    @Test
    @WithMockUser
    void updateMe_BlankPassword_Returns400WithFieldError() throws Exception {
        mockMvc.perform(put("/users/me").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserUpdateRequestDto("Name", "test@example.com", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isArray());
    }

    @Test
    @WithMockUser
    void deleteMe_ExistingUser_Returns204() throws Exception {
        doNothing().when(userService).deleteUser();

        mockMvc.perform(delete("/users/me").with(csrf()))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser();
    }

    @Test
    @WithMockUser
    void deleteMe_UserNotFound_Returns400() throws Exception {
        doThrow(new UserNotFoundException("User not found")).when(userService).deleteUser();

        mockMvc.perform(delete("/users/me").with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }
}
