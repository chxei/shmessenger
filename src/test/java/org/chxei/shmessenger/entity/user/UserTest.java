package org.chxei.shmessenger.entity.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void defaultConstructorInitializesDefaultValues() {
        User user = new User();
        assertTrue(user.isEnabled());
        assertEquals(User.Role.USER, user.getRole());
        assertNotNull(user.getAuthorities());
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("app")));
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void constructorWithUserDetailsCopiesUsernameAndPassword() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("bob")
                .password("secret")
                .authorities("app")
                .build();

        User user = new User(userDetails);
        assertEquals("bob", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertNotNull(user.getAuthorities());
    }

    @Test
    void constructorWithFullFieldsSetsAllFields() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Gender gender = new Gender();
        User user = new User("alice", "Alice Smith", "alice@example.com", now, "pass", gender);

        assertEquals("alice", user.getUsername());
        assertEquals("Alice Smith", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals(now, user.getBirthDate());
        assertEquals("pass", user.getPassword());
        assertEquals(gender, user.getGender());
        assertNotNull(user.getAuthorities());
    }

    @Test
    void constructorWithUsernameEnabledPasswordSetsRoleAuthority() {
        User user = new User("admin_user", true, "pass");

        assertEquals("admin_user", user.getUsername());
        assertTrue(user.isEnabled());
        assertEquals("pass", user.getPassword());

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.contains(new SimpleGrantedAuthority("USER")));
    }

    @Test
    void eraseCredentialsClearsPassword() {
        User user = new User();
        user.setPassword("secret");

        user.eraseCredentials();

        assertNull(user.getPassword());
    }

    @Test
    void equalsAndHashCodeBasedOnUsername() {
        User user1 = new User();
        user1.setUsername("user1");

        User user2 = new User();
        user2.setUsername("user1");

        User user3 = new User();
        user3.setUsername("user2");

        assertEquals(user1, user1);
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "string_object");
    }

    @Test
    void userDetailsContractMethodsDefaultToTrue() {
        User user = new User();
        user.setUsername("testuser");

        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isCredentialsNonExpired());
        assertTrue(user.isEnabled());
    }
}
