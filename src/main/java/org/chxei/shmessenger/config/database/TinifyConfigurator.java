package org.chxei.shmessenger.config.database;

import com.tinify.Tinify;
import org.chxei.shmessenger.utils.Misc;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TinifyConfigurator {
    static {
        System.setProperty("user.timezone", "GMT");
        try {
            Tinify.setKey(Misc.dotenv.get("TINIFY_API_KEY"));
        } catch (Exception e) {
            Misc.logger.info("No tinify api key found");
        }
    }
}
