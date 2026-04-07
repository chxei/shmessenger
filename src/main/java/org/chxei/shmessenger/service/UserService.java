package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.Misc;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.chxei.shmessenger.utils.response.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService, UserDetailsManager, UserDetailsPasswordService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CustomResponseEntity registerUser(User user) {
        user.setPassword(Misc.getPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return new CustomResponseEntity(ResponseType.OK, "You registered successfully");
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            return user.get();
        }

    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Optional<User> tempUser = userRepository.findByUsername(user.getUsername());
        if (tempUser.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + user.getUsername());
        } else {
            tempUser.get().setPassword(Misc.getPasswordEncoder().encode(newPassword));
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
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            userRepository.delete(user.get());
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        // I have no idea how this method should work
    }

    @Override
    public boolean userExists(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }
}
