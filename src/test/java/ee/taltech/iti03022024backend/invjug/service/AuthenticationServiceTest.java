package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.config.ApplicationConfiguration;
import ee.taltech.iti03022024backend.invjug.dto.LoginRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.RegisterRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.TokenResponseDto;
import ee.taltech.iti03022024backend.invjug.entities.InvalidTokenEntity;
import ee.taltech.iti03022024backend.invjug.entities.UserEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.AuthenticationException;
import ee.taltech.iti03022024backend.invjug.repository.InvalidTokenRepository;
import ee.taltech.iti03022024backend.invjug.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvalidTokenRepository invalidTokenRepository;

    @Mock
    private ApplicationConfiguration applicationConfiguration;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Spy
    private SecretKey key;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void test_login_successful_with_valid_credentials() {
        LoginRequestDto request = new LoginRequestDto("testUser", "password123");
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword("hashedPassword");

        given(userRepository.findByUsername("testUser"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "hashedPassword"))
                .willReturn(true);
        given(applicationConfiguration.generateToken(eq(user), any()))
                .willReturn("generatedToken");

        TokenResponseDto response = authenticationService.login(request);

        assertThat(response.token()).isEqualTo("generatedToken");
        verify(userRepository).findByUsername("testUser");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(applicationConfiguration).generateToken(eq(user), any());
    }

    @Test
    void test_login_fails_with_nonexistent_user() {
        LoginRequestDto request = new LoginRequestDto("nonexistentUser", "password123");
        given(userRepository.findByUsername("nonexistentUser"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("User not found");

        verify(userRepository).findByUsername("nonexistentUser");
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(applicationConfiguration);
    }

    @Test
    void test_login_fails_with_invalid_password() {
        LoginRequestDto request = new LoginRequestDto("testUser", "wrongPassword");
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        given(userRepository.findByUsername("testUser"))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .willReturn(false);

        assertThatThrownBy(() -> authenticationService.login(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid password");

        verify(userRepository).findByUsername("testUser");
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verifyNoInteractions(applicationConfiguration);
    }

    @Test
    void test_register_new_user_success() {
        RegisterRequestDto request = new RegisterRequestDto("testuser", "password123", "test@email.com");
        String encodedPassword = "encodedPassword";
        String generatedToken = "generatedToken";

        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByEmail("test@email.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn(encodedPassword);
        given(applicationConfiguration.generateToken(any(UserEntity.class), eq(key))).willReturn(generatedToken);

        TokenResponseDto response = authenticationService.register(request);

        verify(userRepository).save(any(UserEntity.class));
        assertThat(response.token()).isEqualTo(generatedToken);
    }

    @Test
    void test_register_existing_username_throws_exception() {
        RegisterRequestDto request = new RegisterRequestDto("existingUser", "password123", "test@email.com");

        given(userRepository.existsByUsername("existingUser")).willReturn(true);

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Username is already taken");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void test_register_existing_email_throws_exception() {
        RegisterRequestDto request = new RegisterRequestDto("newuser", "password123", "existing@email.com");

        given(userRepository.existsByUsername("newuser")).willReturn(false);
        given(userRepository.existsByEmail("existing@email.com")).willReturn(true);

        assertThatThrownBy(() -> authenticationService.register(request))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Email is already registered");

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void test_logout_bearer_token_is_stripped_and_stored() {
        String token = "Bearer myToken123";
        ArgumentCaptor<InvalidTokenEntity> tokenCaptor = ArgumentCaptor.forClass(InvalidTokenEntity.class);

        authenticationService.logout(token);

        verify(invalidTokenRepository).save(tokenCaptor.capture());
        InvalidTokenEntity savedToken = tokenCaptor.getValue();
        assertEquals("myToken123", savedToken.getToken());
        assertNotNull(savedToken.getInvalidatedAt());
    }
}