package org.chxei.shmessenger.dto.response;

import org.chxei.shmessenger.entity.user.User;

import java.time.LocalDateTime;

public record UserResponse(
        int id,
        String username,
        String name,
        String email,
        String country,
        String phone,
        String pathToProfilePicture,
        String pathToBackgroundPicture,
        LocalDateTime birthDate,
        boolean active,
        String gender
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getCountry() != null ? user.getCountry().getName() : null,
                user.getPhone(),
                user.getPathToProfilePicture(),
                user.getPathToBackgroundPicture(),
                user.getBirthDate() != null ? user.getBirthDate().toLocalDateTime() : null,
                user.isActive(),
                user.getGender() != null ? user.getGender().getName() : null
        );
    }
}
