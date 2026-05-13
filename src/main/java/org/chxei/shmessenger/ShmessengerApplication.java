package org.chxei.shmessenger;

import org.chxei.shmessenger.utils.Misc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShmessengerApplication {
    static void main(String[] args) {
        String version = Runtime.version().toString();
        Misc.logger.info(version);

        SpringApplication.run(ShmessengerApplication.class, args);
    }
}
