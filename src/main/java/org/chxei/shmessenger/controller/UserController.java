package org.chxei.shmessenger.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.chxei.shmessenger.dto.response.UserResponse;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "The User API. Contains operations for user management.")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(UserResponse::fromEntity).toList());
    }

    @GetMapping(value = "/{id}")
    public UserResponse getUser(@PathVariable int id) {
        return UserResponse.fromEntity(userRepository.getReferenceById(id));
    }

    @GetMapping(value = "/username/{userName}")
    public ResponseEntity<Object> getUser(@PathVariable String userName) {
        User user = userRepository.findByUsername(userName).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(UserResponse.fromEntity(user), HttpStatus.OK);
        } else {
            return ResponseEntity.ok(new CustomResponseEntity(ResponseCode.USER_WITH_USERNAME_NOT_FOUND));
        }
    }

    @GetMapping("/testAuth")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }
}
