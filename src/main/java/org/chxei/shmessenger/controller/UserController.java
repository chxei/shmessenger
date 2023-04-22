package org.chxei.shmessenger.controller;

import jakarta.validation.Valid;
import org.chxei.shmessenger.entity.user.AuthenticationResponse;
import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.user.CountryRepository;
import org.chxei.shmessenger.repository.user.GenderRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.repository.user.UserRepositoryCustom;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.chxei.shmessenger.utils.response.ResponseCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final CountryRepository countryRepository;
    private final UserService userService;
    private final UserRepositoryCustom userRepositoryCustom;

    private final JwtEncoder encoder;

    @Autowired
    public UserController(UserRepository userRepository, GenderRepository genderRepository, CountryRepository countryRepository, UserService userService, UserRepositoryCustom userRepositoryCustom, JwtEncoder encoder) {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.countryRepository = countryRepository;
        this.userService = userService;
        this.userRepositoryCustom = userRepositoryCustom;
        this.encoder = encoder;
    }


    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> token(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 36000L;
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue(), 0);
        return ResponseEntity.ok(authenticationResponse);
    }

    @GetMapping(value = "/user/getAll")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/json", value = "/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid User user) {
        return ResponseEntity.ok(userService.registerUser(user));
    }

    @GetMapping(value = "/user/getUserById/{id}")
    public User getUser(@PathVariable int id) {
        return userRepository.getReferenceById(id);
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

    @GetMapping("/testAuth")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }
}
