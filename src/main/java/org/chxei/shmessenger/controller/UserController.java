package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.*;
import org.chxei.shmessenger.repository.CountryRepository;
import org.chxei.shmessenger.repository.GenderRepository;
import org.chxei.shmessenger.repository.UserRepository;
import org.chxei.shmessenger.repository.UserRepositoryCustom;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.JwtUtils;
import org.chxei.shmessenger.utils.Response.CustomResponseEntity;
import org.chxei.shmessenger.utils.Response.ResponseCode;
import org.chxei.shmessenger.utils.Response.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GenderRepository genderRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepositoryCustom userRepositoryCustom;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping(value = "/user/getAll")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(new CustomResponseEntity(ResponseCode.WRONG_USERNAME_PASSWORD));
        }
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json", value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (!userService.registerUser(user)) {
            return ResponseEntity.ok(new CustomResponseEntity(ResponseType.WARNING, "User with this username is already registered"));
        }
        return ResponseEntity.ok(new CustomResponseEntity(ResponseType.OK, "You registered successfully"));
    }

    @GetMapping(value = "/user/getUserById/{id}")
    public User getUser(@PathVariable int id) {
        return userRepository.getOne(id);
    }

    //todo handle exceptions, not found
    @GetMapping(value = "/user/getUserByUsername/{userName}")
    public ResponseEntity<?> getUser(@PathVariable String userName) {
        User user = userRepositoryCustom.findbyusername(userName).orElse(null);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return ResponseEntity.ok(new CustomResponseEntity(ResponseCode.USER_WITH_USERNAME_NOT_FOUND));
        }
    }

    @GetMapping(value = "/country/getAll")
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }

    @GetMapping(value = "/gender/getAll")
    public List<Gender> getGenders() {
        return genderRepository.findAll();
    }
    //todo login
    //todo settings table for user. privacy settings for example
}
