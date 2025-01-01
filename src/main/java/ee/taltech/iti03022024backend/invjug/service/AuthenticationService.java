package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.config.ApplicationConfiguration;
import ee.taltech.iti03022024backend.invjug.dto.LoginRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.RegisterRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.TokenResponseDto;
import ee.taltech.iti03022024backend.invjug.entities.InvalidTokenEntity;
import ee.taltech.iti03022024backend.invjug.entities.Role;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.AuthenticationException;
import ee.taltech.iti03022024backend.invjug.repository.InvalidTokenRepository;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final InvalidTokenRepository invalidTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationConfiguration applicationConfiguration;
    private final SecretKey key;

    public TokenResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new AuthenticationException("username", "User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("password", "Invalid password");
        }

        String token = applicationConfiguration.generateToken(user, key);
        return new TokenResponseDto(token);
    }

    public TokenResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new AuthenticationException("username", "Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new AuthenticationException("email", "Email is already registered");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setEmail(request.email());
        newUser.setRole(Role.ROLE_USER);

        userRepository.save(newUser);

        String token = applicationConfiguration.generateToken(newUser, key);
        log.info("New user created: {}, {}, {}", newUser.getUsername(), newUser.getEmail(), newUser.getRole());
        return new TokenResponseDto(token);
    }

    public void logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        InvalidTokenEntity invalidToken = new InvalidTokenEntity();
        invalidToken.setToken(token);
        invalidToken.setInvalidatedAt(new Date());
        invalidTokenRepository.save(invalidToken);
    }


}
