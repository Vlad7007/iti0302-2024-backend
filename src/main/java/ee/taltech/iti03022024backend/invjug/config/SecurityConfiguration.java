package ee.taltech.iti03022024backend.invjug.config;

import ee.taltech.iti03022024backend.invjug.jwt.JwtRequestFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
public class SecurityConfiguration {

    private JwtRequestFilter jwtRequestFilter;
    private static final String GOD = "ADMIN";

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/categories", "/api/suppliers").hasAnyRole("USER", "MANAGER", GOD)
                        .requestMatchers(HttpMethod.POST, "/api/products", "/api/categories", "/api/suppliers").hasAnyRole("MANAGER", GOD)
                        .requestMatchers("/api/admin/**").hasRole(GOD)

//                        .requestMatchers("/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
//                        .requestMatchers("/api/products").permitAll()
//                        .requestMatchers("/api/categories").permitAll()
//                        .requestMatchers("/api/suppliers").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
