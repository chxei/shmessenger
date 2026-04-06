package org.chxei.shmessenger.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.chxei.shmessenger.entity.user.AuthenticationResponse;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.dto.request.RegisterUserRequest;
import org.chxei.shmessenger.service.UserService;
import org.chxei.shmessenger.utils.response.CustomResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication API. Contains all the operations for login and registering user.")
public class AuthController {

    private final UserService userService;
    private final JwtEncoder encoder;

    @Autowired
    public AuthController(UserService userService, JwtEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Parameter(in = ParameterIn.HEADER,
            name = "Authorization",
            description = "to get jwt for swagger use: curl -XPOST https://localhost:8080/auth/login --user \"user:password\" -k",
            schema = @Schema(type = "string", defaultValue = "Basic <Base64 encoded user:password>")
    )
    public ResponseEntity<AuthenticationResponse> token(Authentication authentication) {
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

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomResponseEntity> registerUser(@RequestBody @Valid RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(request.password());
        user.setBirthDate(request.birthDate());
        
        if (request.countryCode() != null) {
            Country country = new Country();
            country.setCode(request.countryCode());
            user.setCountry(country);
        }
        
        if (request.genderId() != null) {
            Gender gender = new Gender();
            gender.setId(request.genderId());
            user.setGender(gender);
        }
        
        return ResponseEntity.ok(userService.registerUser(user));
    }
}
