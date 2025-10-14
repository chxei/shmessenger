package org.chxei.shmessenger;

import org.chxei.shmessenger.utils.Misc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.chxei.shmessenger")
public class ShmessengerApplication {
    static void main(String[] args) {
        String version = Runtime.version().toString();
        Misc.logger.info(version);

        SpringApplication.run(ShmessengerApplication.class, args);
    }

}
