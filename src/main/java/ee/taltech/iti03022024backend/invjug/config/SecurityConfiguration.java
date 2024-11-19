package ee.taltech.iti03022024backend.invjug.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.entities.Role;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.ErrorResponse;
import ee.taltech.iti03022024backend.invjug.jwt.JwtRequestFilter;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@AllArgsConstructor
@Configuration
public class SecurityConfiguration {

    private JwtRequestFilter jwtRequestFilter;
    private final UserRepository userRepository;
    private static final String GOD = "ADMIN";
    @Autowired
    private Environment env;

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
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            ErrorResponse errorResponse = new ErrorResponse("Permission denied");
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                        })
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CommandLineRunner createDefaultAdminUser() {
        return args -> {
            String adminUsername = env.getProperty("admin.username", "admin");
            String adminPassword = env.getProperty("admin.password", "12345678");
            String adminEmail = env.getProperty("admin.email", "admin@example.com");
            log.info("Trying to create default admin user: {}", adminUsername);

            if (userRepository.findByRole(Role.ROLE_ADMIN).isEmpty()) {
                UserEntity adminUser = new UserEntity();
                adminUser.setUsername(adminUsername);
                adminUser.setPassword(passwordEncoder().encode(adminPassword));
                adminUser.setEmail(adminEmail);
                adminUser.setRole(ee.taltech.iti03022024backend.invjug.entities.Role.ROLE_ADMIN);
                userRepository.save(adminUser);
            }
        };
    }

}
