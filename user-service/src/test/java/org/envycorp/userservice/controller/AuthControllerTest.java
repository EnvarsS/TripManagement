package org.envycorp.userservice.controller;

import org.envycorp.userservice.controllers.AuthController;
import org.envycorp.userservice.exceptions.EmailIsAlreadyTakenException;
import org.envycorp.userservice.exceptions.IncorrectEmailException;
import org.envycorp.userservice.exceptions.IncorrectPasswordException;
import org.envycorp.userservice.filter.JwtFilter;
import org.envycorp.userservice.models.dto.request.UserCreateRequestDto;
import org.envycorp.userservice.models.dto.request.UserLoginRequestDto;
import org.envycorp.userservice.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)
)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @WithMockUser
    void login_ValidCredentials_Returns200WithToken() throws Exception {
        when(authService.login(any())).thenReturn(ResponseEntity.ok("jwt-token"));

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequestDto("test@example.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }

    @Test
    @WithMockUser
    void login_WrongEmail_Returns400() throws Exception {
        when(authService.login(any()))
                .thenThrow(new IncorrectEmailException("Invalid email or password"));

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequestDto("wrong@example.com", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email or password"));
    }

    @Test
    @WithMockUser
    void login_WrongPassword_Returns400() throws Exception {
        when(authService.login(any()))
                .thenThrow(new IncorrectPasswordException("Invalid password"));

        mockMvc.perform(post("/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequestDto("test@example.com", "wrongpass"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid password"));
    }

    @Test
    @WithMockUser
    void register_ValidRequest_Returns200WithToken() throws Exception {
        when(authService.createUser(any())).thenReturn(ResponseEntity.ok("new-token"));

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("new@example.com", "password123", "New User"))))
                .andExpect(status().isOk())
                .andExpect(content().string("new-token"));
    }

    @Test
    @WithMockUser
    void register_DuplicateEmail_Returns400() throws Exception {
        when(authService.createUser(any()))
                .thenThrow(new EmailIsAlreadyTakenException("Email Already Exists"));

        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("taken@example.com", "password123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email Already Exists"));
    }

    @Test
    @WithMockUser
    void register_BlankEmail_Returns400WithFieldError() throws Exception {
        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("", "password123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isArray());
    }

    @Test
    @WithMockUser
    void register_InvalidEmailFormat_Returns400() throws Exception {
        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("not-an-email", "password123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").isArray());
    }

    @Test
    @WithMockUser
    void register_PasswordTooShort_Returns400() throws Exception {
        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("test@example.com", "123", "User"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").isArray());
    }

    @Test
    @WithMockUser
    void register_BlankName_Returns400() throws Exception {
        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("test@example.com", "password123", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isArray());
    }

    @Test
    @WithMockUser
    void register_NameTooShort_Returns400() throws Exception {
        mockMvc.perform(post("/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequestDto("test@example.com", "password123", "A"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").isArray());
    }
}
