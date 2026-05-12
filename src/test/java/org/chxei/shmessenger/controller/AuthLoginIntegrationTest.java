package org.chxei.shmessenger.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end login against the real {@link org.springframework.security.core.userdetails.UserDetailsService} and DB seed
 * ({@link org.chxei.shmessenger.config.database.DatabaseFiller}). Catches SQLite PK / identity DDL issues that break
 * {@code findByUsername} after insert.
 */
@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {

    private static final String USERNAME = "chxei";
    private static final String PASSWORD = "chxei";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithBasicAuth_returnsJwt() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt", notNullValue()))
                .andExpect(jsonPath("$.jwt",
                        matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")));
    }
}
