package org.chxei.shmessenger.entity.user;

public record AuthenticationResponse(String jwt, int userId) {
}
