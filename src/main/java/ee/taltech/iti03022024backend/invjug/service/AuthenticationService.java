package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.config.ApplicationConfiguration;
import ee.taltech.iti03022024backend.invjug.dto.LoginRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.RegisterRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.TokenResponseDto;
import ee.taltech.iti03022024backend.invjug.entities.Role;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.crypto.SecretKey;

@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApplicationConfiguration applicationConfiguration;
    private final SecretKey key;

    public AuthenticationService(UserRepository userRepository,
                                 BCryptPasswordEncoder passwordEncoder,
                                 ApplicationConfiguration applicationConfiguration,
                                 SecretKey key) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.applicationConfiguration = applicationConfiguration;
        this.key = key;
    }

    public TokenResponseDto login(LoginRequestDto request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String token = applicationConfiguration.generateToken(user, key);
        return new TokenResponseDto(token);
    }

    public TokenResponseDto register(@Valid @RequestBody RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setEmail(request.email());
        newUser.setRole(Role.ROLE_USER);

        userRepository.save(newUser);

        String token = applicationConfiguration.generateToken(newUser, key);
        return new TokenResponseDto(token);
    }

}
