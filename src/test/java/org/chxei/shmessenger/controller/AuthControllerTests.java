package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    @WithMockUser(username = "alice", authorities = "app")
    void loginReturnsJwtAndUserId() throws Exception {
        User user = new User();
        user.setId(7);
        user.setUsername("alice");

        when(userService.loadUserByUsername("alice")).thenReturn(user);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt("token-value"));

        mockMvc.perform(post("/auth/login").principal(new TestingAuthenticationToken("alice", "password", "app")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").value("token-value"))
                .andExpect(jsonPath("$.userId").value(7));

        verify(userService).loadUserByUsername("alice");
        verify(jwtEncoder).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void registerMapsRequestToUserEntity() throws Exception {
        when(userService.registerUser(any(User.class)))
                .thenReturn(new CustomResponseEntity(ResponseType.OK, "registered"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "name": "Alice",
                                  "email": "alice@example.com",
                                  "phone": "555",
                                  "password": "secret",
                                  "countryCode": "GE",
                                  "genderId": 2,
                                  "birthDate": "2024-01-02T03:04:05.000+00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.responseType").value("OK"))
                .andExpect(jsonPath("$.message").value("registered"));

        verify(userService).registerUser(argThat(user -> {
            assertEquals("alice", user.getUsername());
            assertEquals("Alice", user.getName());
            assertEquals("alice@example.com", user.getEmail());
            assertEquals("555", user.getPhone());
            assertEquals("secret", user.getPassword());
            assertEquals("GE", user.getCountry().getCode());
            assertEquals(2, user.getGender().getId());
            return true;
        }));
    }

    @Test
    void registerRejectsBlankPassword() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "alice",
                                  "name": "Alice",
                                  "email": "alice@example.com",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(userService, never()).registerUser(any());
    }

    @Test
    void changePasswordReturnsSuccess() throws Exception {
        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "old",
                                  "newPassword": "new"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.responseType").value("OK"))
                .andExpect(jsonPath("$.message", notNullValue()));

        verify(userService).changePassword("old", "new");
    }

    @Test
    void changePasswordRejectsBlankPassword() throws Exception {
        mockMvc.perform(put("/auth/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "oldPassword": "",
                                  "newPassword": "new"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(userService, never()).changePassword(any(), any());
    }

    private Jwt jwt(String tokenValue) {
        Instant now = Instant.now();
        return new Jwt(tokenValue, now, now.plusSeconds(60), java.util.Map.of("alg", "none"), java.util.Map.of("sub", "alice"));
    }
}
