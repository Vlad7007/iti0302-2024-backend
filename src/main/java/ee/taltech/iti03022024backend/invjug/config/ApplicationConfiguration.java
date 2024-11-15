package ee.taltech.iti03022024backend.invjug.config;

import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

    @Bean
    public SecretKey jwtKey() {
        return Jwts.SIG.HS256.key().build();
    }

    public String generateToken(UserEntity user, SecretKey key) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of(
                        "userId", user.getId()
                ))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(key)
                .compact();
    }

}
