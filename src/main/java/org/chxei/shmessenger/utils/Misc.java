package org.chxei.shmessenger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Misc {
    public static final Logger logger = LoggerFactory.getLogger(Misc.class);
    public static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    private Misc() {
    }

    public static @Nullable String stringToMd5(String source) {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source.getBytes());
            byte[] bytes = md.digest();
            var sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException _) {
            logger.error("Error while hashing password");
            return null;
        }
    }

    public static @Nullable String convertObjectToJson(@Nullable Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        var mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    @SafeVarargs
    public static <T> @Nullable T coalesce(T... params) {
        for (T param : params) {
            if (param != null) {
                return param;
            }
        }
        return null;
    }
}
