package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.User;
import org.chxei.shmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsAuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepositoryInt;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepositoryInt.findByUserName(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Not found: " + username);
        } else {
            return user.map(UserDetailsAuth::new).get();
        }

    }
}
