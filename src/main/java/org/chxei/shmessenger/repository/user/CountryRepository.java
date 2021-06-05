package org.chxei.shmessenger.repository.user;

import org.chxei.shmessenger.entity.user.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Integer> {
}
