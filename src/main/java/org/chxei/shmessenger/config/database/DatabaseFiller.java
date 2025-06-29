package org.chxei.shmessenger.config.database;

import org.chxei.shmessenger.entity.chat.MessageType;
import org.chxei.shmessenger.entity.user.Country;
import org.chxei.shmessenger.entity.user.Gender;
import org.chxei.shmessenger.entity.user.User;
import org.chxei.shmessenger.repository.chat.MessageTypeRepository;
import org.chxei.shmessenger.repository.user.CountryRepository;
import org.chxei.shmessenger.repository.user.GenderRepository;
import org.chxei.shmessenger.repository.user.UserRepository;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseFiller {
    private final UserRepository userRepository;
    private final GenderRepository genderRepository;
    private final CountryRepository countryRepository;
    private final MessageTypeRepository messageTypeRepository;

    public DatabaseFiller(UserRepository userRepository, GenderRepository genderRepository, CountryRepository countryRepository, MessageTypeRepository messageTypeRepository) {
        this.userRepository = userRepository;
        this.genderRepository = genderRepository;
        this.countryRepository = countryRepository;
        this.messageTypeRepository = messageTypeRepository;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<Gender> genders = populateGenders();
        List<Country> countries = populateCountries();
        populateMessageType();

        final String DEF_USER = "chxei";
        User user = new User(DEF_USER, DEF_USER, DEF_USER + "@" + DEF_USER + ".org", Timestamp.valueOf("2000-01-01 00:00:00"), DEF_USER, genders.getFirst());
        user.setCountry(countries.getFirst());
        user.setPassword(Misc.getPasswordEncoder().encode(user.getPassword()));
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

    public void populateMessageType() {
        List<MessageType> messageTypes = new ArrayList<>();
        for (String s : Arrays.asList("EMOJI", "COMPOSED", "GIF", "MEDIA_GIF", "MEDIA_VIDEO", "MEDIA_PHOTO", "MEDIA_VOICE", "MEDIA_FILE")) {
            messageTypes.add(new MessageType(s));
        }
        messageTypeRepository.saveAll(messageTypes);
    }
}
