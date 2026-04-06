package org.chxei.shmessenger.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.repository.user.GenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/genders")
@Tag(name = "Gender", description = "The Gender API.")
public class GenderController {

    private final GenderRepository genderRepository;

    @Autowired
    public GenderController(GenderRepository genderRepository) {
        this.genderRepository = genderRepository;
    }

    @GetMapping
    public List<Gender> getGenders() {
        return genderRepository.findByStatus(true);
    }
}
