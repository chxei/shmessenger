package org.chxei.shmessenger.controller;

import lombok.extern.slf4j.Slf4j;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    private static final String USERNAME = "chxei1";
    private static final String PASSWORD = "chxei1";
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    public void testWithBasicAuth_success() throws Exception {
        var user = new User(USERNAME, true, PASSWORD);
        user.setId(1);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(user(USERNAME).roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt", notNullValue()))
                .andExpect(
                        jsonPath("$.jwt", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        log.info("JWT Response: {}", response);
    }

    @Test
    public void testLoginWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testLoginWithMockUser() throws Exception {
        var user = new User(USERNAME, true, PASSWORD);
        user.setId(1);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(user);

        mockMvc.perform(post("/auth/login")
                        .with(user(USERNAME).roles("USER"))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.jwt", notNullValue()));
    }
}
