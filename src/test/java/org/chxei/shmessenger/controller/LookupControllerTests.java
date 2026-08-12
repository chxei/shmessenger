package org.chxei.shmessenger.controller;

import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.repository.user.CountryRepository;
import org.chxei.shmessenger.repository.user.GenderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({CountryController.class, GenderController.class})
class LookupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryRepository countryRepository;

    @MockitoBean
    private GenderRepository genderRepository;

    @Test
    void countriesReturnsRepositoryResults() throws Exception {
        when(countryRepository.findAll()).thenReturn(List.of(new Country("GE", "Georgia")));

        mockMvc.perform(get("/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("GE"))
                .andExpect(jsonPath("$[0].name").value("Georgia"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(countryRepository).findAll();
    }

    @Test
    void gendersReturnsActiveRepositoryResults() throws Exception {
        when(genderRepository.findAllByActive(true)).thenReturn(List.of(new Gender(1, "Female", true)));

        mockMvc.perform(get("/genders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Female"))
                .andExpect(jsonPath("$[0].active").value(true));

        verify(genderRepository).findAllByActive(true);
    }
}
