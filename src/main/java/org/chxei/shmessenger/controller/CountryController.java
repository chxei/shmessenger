package org.chxei.shmessenger.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.repository.user.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
@Tag(name = "Country", description = "The Country API.")
public class CountryController {

    private final CountryRepository countryRepository;

    @Autowired
    public CountryController(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public List<Country> getCountries() {
        return countryRepository.findAll();
    }
}
