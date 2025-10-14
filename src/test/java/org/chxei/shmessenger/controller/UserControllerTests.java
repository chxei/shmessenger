package org.chxei.shmessenger.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    private static final String USERNAME = "chxei";
    private static final String PASSWORD = "chxei";
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testWithBasicAuth_success() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt", notNullValue()))
                .andExpect(jsonPath("$.jwt", matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$")))
                .andReturn();

        String response = result.getResponse().getContentAsString();


        System.out.println("JWT Response: " + response);
    }

    @Test
    public void testLoginWithoutAuth_Unauthorized() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD, roles = "USER")
    public void testLoginWithMockUser() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.jwt", notNullValue()));
    }
}
