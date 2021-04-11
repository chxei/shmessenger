package org.chxei.shmessenger.repository;

import org.chxei.shmessenger.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
