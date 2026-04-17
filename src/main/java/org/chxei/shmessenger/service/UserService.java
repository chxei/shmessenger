package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.chxei.shmessenger.utils.response.ResponseType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService, UserDetailsManager, UserDetailsPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CustomResponseEntity registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION);
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new CustomResponseEntity(ResponseType.OK, "You registered successfully");
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            return user.get();
        }

    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        var tempUser = userRepository.findByUsername(user.getUsername());
        if (tempUser.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + user.getUsername());
        } else {
            tempUser.get().setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(tempUser.get());
        }
        return tempUser.get();
    }

    @Override
    public void createUser(UserDetails user) {
        userRepository.save(new User(user));
    }

    @Override
    public void updateUser(UserDetails user) {
        userRepository.save(new User(user));
    }

    @Override
    public void deleteUser(String username) {
        var user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            userRepository.delete(user.get());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            throw new AccessDeniedException("No authenticated user found in context layer.");
        }

        var userOpt = userRepository.findByUsername(currentUser.getName());
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Current user not found in database: " + currentUser.getName());
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Incorrect current password.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        var user = userRepository.findByUsername(username);
        return user.isPresent();
    }
}
