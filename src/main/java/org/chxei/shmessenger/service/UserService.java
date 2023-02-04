package org.chxei.shmessenger.service;

import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.Misc;
import org.chxei.shmessenger.utils.Response.CustomResponseEntity;
import org.chxei.shmessenger.utils.Response.ResponseCode;
import org.chxei.shmessenger.utils.Response.ResponseType;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

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
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof PSQLException pe && "23505".equals(pe.getSQLState())) {
                ServerErrorMessage postgresError = pe.getServerErrorMessage();
                if (postgresError != null) {
                    String constraint = postgresError.getConstraint();
                    if (constraint != null) {
                        return switch (constraint.toUpperCase()) {
                            case "CONSTRAINT_UNIQUE_USERS_USERNAME" ->
                                    new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_USERNAME_VIOLATION);
                            case "CONSTRAINT_UNIQUE_USERS_EMAIL" ->
                                    new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_EMAIL_VIOLATION);
                            case "CONSTRAINT_UNIQUE_USERS_PHONE" ->
                                    new CustomResponseEntity(ResponseCode.CONSTRAINT_UNIQUE_USERS_PHONE_VIOLATION);
                            default ->
                                    new CustomResponseEntity(ResponseType.PIZDEC, ResponseCode.USER_UNKNOWN_CONSTRAINT_ERROR, "User creation failed: " + constraint);
                        };
                    }
                    return new CustomResponseEntity(ResponseCode.USER_UNIQUE_CONSTRAINT_VIOLATION, postgresError.getDetail());
                }
            }

        }
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
        return null;
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }
}
