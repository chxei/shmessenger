package org.chxei.shmessenger.controller;

import lombok.extern.slf4j.Slf4j;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    private static final String USERNAME = "chxei";
    private static final String PASSWORD = "chxei";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserService userService;

    @Test
    public void testWithBasicAuth_success() throws Exception {
        var user = new User(USERNAME, true, passwordEncoder.encode(PASSWORD));
        user.setId(1);
        when(userService.loadUserByUsername(USERNAME)).thenReturn(user);

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .with(httpBasic(USERNAME, PASSWORD))
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

}
