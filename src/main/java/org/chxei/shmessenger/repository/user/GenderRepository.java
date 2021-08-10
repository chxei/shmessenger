package org.chxei.shmessenger.repository.user;

import org.chxei.shmessenger.entity.user.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
    @Query("SELECT e FROM genders e WHERE e.active=?1")
    List<Gender> findByStatus(Boolean active);
}
