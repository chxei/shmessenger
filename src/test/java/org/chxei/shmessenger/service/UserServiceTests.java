package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUserRejectsDuplicateUsername() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        CustomResponseEntity response = userService.registerUser(user);

        assertEquals("CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION", response.getResponseCode().name());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserRejectsDuplicateEmail() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        CustomResponseEntity response = userService.registerUser(user);

        assertEquals("CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION", response.getResponseCode().name());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserRejectsDuplicatePhone() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("555")).thenReturn(true);

        CustomResponseEntity response = userService.registerUser(user);

        assertEquals("CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION", response.getResponseCode().name());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUserEncodesPasswordAndSaves() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.existsByPhone("555")).thenReturn(false);
        when(passwordEncoder.encode("raw")).thenReturn("encoded");

        CustomResponseEntity response = userService.registerUser(user);

        assertEquals("OK", response.getResponseType().name());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.captor();
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded", userCaptor.getValue().getPassword());
    }

    @Test
    void loadUserByUsernameReturnsUser() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertSame(user, userService.loadUserByUsername("alice"));
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("missing"));
    }

    @Test
    void updatePasswordEncodesAndSaves() {
        User user = user("alice", "alice@example.com", "555", "old");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        UserDetails updated = userService.updatePassword(user, "new");

        assertSame(user, updated);
        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updatePasswordThrowsWhenUserMissing() {
        User user = user("missing", "missing@example.com", "555", "old");
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.updatePassword(user, "new"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createAndUpdateUserSaveConvertedUserDetails() {
        User user = user("alice", "alice@example.com", "555", "raw");

        userService.createUser(user);
        userService.updateUser(user);

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void deleteUserDeletesExistingUser() {
        User user = user("alice", "alice@example.com", "555", "raw");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        userService.deleteUser("alice");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUserThrowsWhenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser("missing"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void changePasswordEncodesAndSavesForAuthenticatedUser() {
        User user = user("alice", "alice@example.com", "555", "encoded-old");
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("alice", "ignored"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encoded-old")).thenReturn(true);
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        userService.changePassword("old", "new");

        assertEquals("encoded-new", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRequiresAuthenticatedUser() {
        assertThrows(AccessDeniedException.class, () -> userService.changePassword("old", "new"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordThrowsWhenCurrentUserMissing() {
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("missing", "ignored"));
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.changePassword("old", "new"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePasswordRejectsWrongOldPassword() {
        User user = user("alice", "alice@example.com", "555", "encoded-old");
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("alice", "ignored"));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-old")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.changePassword("wrong", "new"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void userExistsReflectsRepositoryLookup() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user("alice", "alice@example.com", "555", "raw")));
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertTrue(userService.userExists("alice"));
        assertFalse(userService.userExists("missing"));
    }

    private User user(String username, String email, String phone, String password) {
        User user = new User();
        user.setUsername(username);
        user.setName(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(password);
        return user;
    }
}
