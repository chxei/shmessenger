package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"server.ssl.enabled=false", "spring.main.lazy-initialization=false"})
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerLoadUpdatePasswordAndExistsUseRealRepository() {
        String username = unique("svc_user");
        User user = user(username, "rawPassword");

        var response = userService.registerUser(user);

        assertEquals("OK", response.getResponseType().name());
        assertTrue(userService.userExists(username));

        User loaded = userService.loadUserByUsername(username);
        assertTrue(passwordEncoder.matches("rawPassword", loaded.getPassword()));

        userService.updatePassword(loaded, "updatedPassword");

        User updated = userRepository.findByUsername(username).orElseThrow();
        assertTrue(passwordEncoder.matches("updatedPassword", updated.getPassword()));
    }

    @Test
    void createUpdateDeleteUserUseRealRepository() {
        String username = unique("manager");
        User user = user(username, "password");

        userService.createUser(user);
        assertTrue(userRepository.findByUsername(username).isPresent());

        user.setPassword("changed");
        userService.updateUser(user);
        assertEquals("changed", userRepository.findByUsername(username).orElseThrow().getPassword());

        userService.deleteUser(username);
        assertFalse(userRepository.findByUsername(username).isPresent());
    }

    @Test
    void changePasswordUsesAuthenticatedUserFromSecurityContext() {
        String username = unique("change");
        User user = user(username, "rawPassword");
        userService.registerUser(user);

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(username, "ignored"));

        userService.changePassword("rawPassword", "newPassword");

        User updated = userRepository.findByUsername(username).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword", updated.getPassword()));
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(unique("missing")));
    }

    private User user(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setName(username);
        user.setEmail(username + "@example.com");
        user.setPhone(username);
        user.setPassword(password);
        return user;
    }

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
    }
}
