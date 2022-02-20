package org.chxei.shmessenger;

import com.tinify.Tinify;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.chxei.shmessenger")
public class ShmessengerApplication {
    static {
        System.setProperty("user.timezone", "GMT");
        try {
            Tinify.setKey(Misc.dotenv.get("TINIFY_API_KEY"));
        } catch (Exception e) {
            Misc.logger.info("No tinify api key found");
        }
    }

    public static void main(String[] args) {
        Misc.logger.info(Runtime.version().toString());
        SpringApplication.run(ShmessengerApplication.class, args);
    }

}
