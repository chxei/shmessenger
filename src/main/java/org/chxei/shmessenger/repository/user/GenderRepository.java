package org.chxei.shmessenger.repository.user;

import org.chxei.shmessenger.entity.user.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
}
