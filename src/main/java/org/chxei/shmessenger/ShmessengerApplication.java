package org.chxei.shmessenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories("org.chxei.shmessenger")
public class ShmessengerApplication {

    public static void main(String[] args) {
        System.setProperty("user.timezone", "GMT");
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        SpringApplication.run(ShmessengerApplication.class, args);
    }

}
