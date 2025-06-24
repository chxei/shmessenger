package org.chxei.shmessenger.repository.user;

import org.chxei.shmessenger.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username); //automatically makes a query for a column named 'username'
}
