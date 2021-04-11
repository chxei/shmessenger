package org.chxei.shmessenger.repository;

import org.chxei.shmessenger.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
}
