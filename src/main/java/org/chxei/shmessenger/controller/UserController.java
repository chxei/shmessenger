package org.chxei.shmessenger.controller;

import jakarta.validation.Valid;
import org.chxei.shmessenger.entity.user.*;
import org.chxei.shmessenger.repository.user.CountryRepository;
import org.chxei.shmessenger.repository.user.GenderRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.repository.user.UserRepositoryCustom;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.JwtUtils;
import org.chxei.shmessenger.utils.Response.CustomResponseEntity;
import org.chxei.shmessenger.utils.Response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final CountryRepository countryRepository;
    private final UserService userService;
    private final UserRepositoryCustom userRepositoryCustom;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserRepository userRepository, GenderRepository genderRepository, CountryRepository countryRepository, UserService userService, UserRepositoryCustom userRepositoryCustom, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.countryRepository = countryRepository;
        this.userService = userService;
        this.userRepositoryCustom = userRepositoryCustom;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping(value = "/user/getAll")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.ok(new CustomResponseEntity(ResponseCode.WRONG_USERNAME_PASSWORD));
        }
        final User userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt, userDetails.getId()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json", value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @GetMapping(value = "/user/getUserById/{id}")
    public User getUser(@PathVariable int id) {
        return userRepository.getById(id);
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
        return genderRepository.findByStatus(true);
    }
}
