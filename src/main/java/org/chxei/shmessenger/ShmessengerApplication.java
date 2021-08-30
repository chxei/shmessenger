package org.chxei.shmessenger;

import com.tinify.Tinify;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.KeyStoreException;

@SpringBootApplication
@EnableJpaRepositories("org.chxei.shmessenger")
public class ShmessengerApplication {
    static {
        System.setProperty("user.timezone", "GMT");
        Tinify.setKey(Misc.dotenv.get("TINIFY_API_KEY"));
        nu.pattern.OpenCV.loadLocally();
    }

    public static void main(String[] args) throws KeyStoreException {
        SpringApplication.run(ShmessengerApplication.class, args);
    }

}
