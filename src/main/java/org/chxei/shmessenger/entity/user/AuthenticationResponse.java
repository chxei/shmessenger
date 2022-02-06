package org.chxei.shmessenger.entity.user;

import lombok.Data;

@Data
public final class AuthenticationResponse {
    private final String jwt;
    private final int userId;
}
