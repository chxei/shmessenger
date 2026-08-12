package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "alice")
class UserControllerSliceTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void getAllUsersReturnsUserResponses() throws Exception {
        when(userRepository.findAll()).thenReturn(List.of(user(1, "alice")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].country").value("Georgia"))
                .andExpect(jsonPath("$[0].gender").value("Female"));

        verify(userRepository).findAll();
    }

    @Test
    void getByIdReturnsUserWhenFound() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.of(user(1, "alice")));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getByIdReturnsCustomResponseWhenMissing() throws Exception {
        when(userRepository.findById(404)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/404"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("USER_WITH_ID_NOT_FOUND"))
                .andExpect(jsonPath("$.responseType").value("WARNING"));
    }

    @Test
    void getByUsernameReturnsUserWhenFound() throws Exception {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user(1, "alice")));

        mockMvc.perform(get("/users/username/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("alice"));
    }

    @Test
    void getByUsernameReturnsCustomResponseWhenMissing() throws Exception {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/username/missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseCode").value("USER_WITH_USERNAME_NOT_FOUND"))
                .andExpect(jsonPath("$.responseType").value("WARNING"));
    }

    @Test
    void testAuthReturnsAuthenticatedUserName() throws Exception {
        mockMvc.perform(get("/users/testAuth").principal(new TestingAuthenticationToken("alice", "password", "app")))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, alice!"));
    }

    private User user(int id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPhone("555");
        user.setCountry(new Country("GE", "Georgia"));
        user.setGender(new Gender(1, "Female", true));
        return user;
    }
}
