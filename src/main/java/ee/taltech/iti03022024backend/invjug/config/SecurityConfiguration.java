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
import org.springframework.util.StringUtils;

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
    public CommandLineRunner checkForDefaultAdminUser() {
        return args -> {
            if (userRepository.findByRole(Role.ROLE_ADMIN).isEmpty()) {
                String adminUsername = env.getProperty("admin.username");
                String adminPassword = env.getProperty("admin.password");
                String adminEmail = env.getProperty("admin.email");
                log.info("Creating default admin user: {}", adminUsername);

                if (StringUtils.hasText(adminUsername) && StringUtils.hasText(adminPassword) && StringUtils.hasText(adminEmail)) {
                    createAdminUser(adminUsername, adminPassword, adminEmail);
                } else {
                    log.warn("One or more admin properties in application.properties are missing");
                    log.warn("Using default admin credentials: admin/12345678");
                    createAdminUser("admin", "12345678", "admin@example.com");
                }
            }
        };
    }

    private void createAdminUser(String username, String password, String email) {
        UserEntity adminUser = new UserEntity();
        adminUser.setUsername(username);
        adminUser.setPassword(passwordEncoder().encode(password));
        adminUser.setEmail(email);
        adminUser.setRole(Role.ROLE_ADMIN);
        userRepository.save(adminUser);
        log.info("Created default admin user: {}", username);
    }

}
