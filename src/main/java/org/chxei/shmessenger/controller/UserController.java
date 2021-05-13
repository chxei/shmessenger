package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.*;
import org.chxei.shmessenger.repository.CountryRepository;
import org.chxei.shmessenger.repository.GenderRepository;
import org.chxei.shmessenger.repository.UserRepository;
import org.chxei.shmessenger.repository.UserRepositoryCustom;
import org.chxei.shmessenger.service.UserDetailsAuthService;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.JwtUtils;
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
    private UserDetailsAuthService userDetailsAuthService;
    @Autowired
    private JwtUtils jwtUtils;

    @RequestMapping("/user/getAll")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = userDetailsAuthService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json", value = "/register")
    public void registerUser(@RequestBody User user) {
        userService.registerUser(user);
    }

    @GetMapping(value = "/user/getUserById/{id}")
    public User getUser(@PathVariable int id) {
        return userRepository.getOne(id);
    }

    //todo handle exceptions, not found
    @GetMapping(value = "/user/{userName}")
    public ResponseEntity<User> getUser(@PathVariable String userName) {
        User user = userRepositoryCustom.findByUserName(userName).orElse(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
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
