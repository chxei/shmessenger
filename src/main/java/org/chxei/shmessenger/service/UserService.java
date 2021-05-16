package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.User;
import org.chxei.shmessenger.repository.UserRepository;
import org.chxei.shmessenger.repository.UserRepositoryCustom;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRepositoryCustom userRepositoryCustom;

    private UserService() {
    }

    public boolean registerUser(User user) {
        user.setPassword(Misc.getPasswordEncoder().encode(user.getPassword()));
        if (userRepositoryCustom.existsByUsername(user.getUsername())) {
            return false;
        } else {
            userRepository.save(user);
            return true;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            return user.get();
        }

    }
}
