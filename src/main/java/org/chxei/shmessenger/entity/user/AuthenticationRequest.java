package org.chxei.shmessenger.entity.user;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
