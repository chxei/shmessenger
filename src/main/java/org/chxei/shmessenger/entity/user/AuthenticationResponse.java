package org.chxei.shmessenger.entity.user;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private final String jwt;
}
