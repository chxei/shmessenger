package org.chxei.shmessenger.repository.user;

import org.chxei.shmessenger.entity.user.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
    List<Gender> findAllByActive(boolean active);
}
