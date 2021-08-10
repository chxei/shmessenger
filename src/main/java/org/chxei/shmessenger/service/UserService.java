package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.repository.user.UserRepositoryCustom;
import org.chxei.shmessenger.utils.Misc;
import org.chxei.shmessenger.utils.Response.CustomResponseEntity;
import org.chxei.shmessenger.utils.Response.ResponseCode;
import org.chxei.shmessenger.utils.Response.ResponseType;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    public CustomResponseEntity registerUser(User user) {
        user.setPassword(Misc.getPasswordEncoder().encode(user.getPassword()));
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof PSQLException pe) {
                if ("23505".equals(pe.getSQLState())) {
                    ServerErrorMessage postgresError = pe.getServerErrorMessage();
                    if (postgresError != null) {
                        String constraint = postgresError.getConstraint();
                        if (constraint != null) {
                            switch (constraint.toUpperCase()) {
                                case "CONSTRAINT_UNIQUE_USERS_USERNAME":
                                    return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION);
                                case "CONSTRAINT_UNIQUE_USERS_EMAIL":
                                    return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION);
                                case "CONSTRAINT_UNIQUE_USERS_PHONE":
                                    return new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION);
                            }
                        }
                        return new CustomResponseEntity(ResponseCode.USER_UNIQUE_CONSTRAINT_VIOLATION, postgresError.getDetail());
                    }
                }
            }

        }
        return new CustomResponseEntity(ResponseType.OK, "You registered successfully");
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
