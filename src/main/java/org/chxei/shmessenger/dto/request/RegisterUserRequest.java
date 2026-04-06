package org.chxei.shmessenger.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.sql.Timestamp;

public record RegisterUserRequest(
    @NotEmpty String username,
    @NotEmpty String name,
    @Email String email,
    String phone,
    String password,
    String countryCode,
    Integer genderId,
    Timestamp birthDate
) {}
