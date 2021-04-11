package org.chxei.shmessenger.config;

import org.chxei.shmessenger.entity.Country;
import org.chxei.shmessenger.entity.Gender;
import org.chxei.shmessenger.entity.User;
import org.chxei.shmessenger.repository.CountryRepository;
import org.chxei.shmessenger.repository.GenderRepository;
import org.chxei.shmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class DatabasePopulator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private CountryRepository countryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Gender> genders = populateGenders();
        List<Country> countries = populateCountries();

        User user = new User("chxei", "chxei", "chxei@chxei.org", Timestamp.valueOf("2000-01-01 00:00:00"), "chxei", genders.get(0));
        user.setCountry(countries.get(0));
        userRepository.save(user);
    }

    public List<Gender> populateGenders() {
        List<Gender> genders = new ArrayList<>();
        genders.add(new Gender("OTHER"));
        genders.add(new Gender("MALE"));
        genders.add(new Gender("FEMALE"));
        genderRepository.saveAll(genders);

        return genders;
    }

    public List<Country> populateCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("GE", "Georgia"));
        countries.add(new Country("US", "United States"));
        countries.add(new Country("RU", "Russian federation"));
        countryRepository.saveAll(countries);
        return countries;
    }
}
