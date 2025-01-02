package ee.taltech.iti03022024backend.invjug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.invjug.dto.LoginRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.RegisterRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.TokenResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void test_valid_login_returns_token() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");

        mockMvc.perform(post(BASE_URL + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void test_unknown_username_returns_401() throws Exception {
        LoginRequestDto request = new LoginRequestDto("unknownUser", "password123");

        mockMvc.perform(post(BASE_URL + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.username").value("User not found"));
    }

    @Test
    void test_invalid_password_returns_400() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "invalid");

        mockMvc.perform(post(BASE_URL + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be between 8 and 255 characters"));
    }

    @Test
    void test_valid_registration_returns_token() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto(
                "newuser",
                "password123",
                "test@example.com"
        );

        mockMvc.perform(post(BASE_URL + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void test_registration_with_existing_username_fails() throws Exception {
        RegisterRequestDto user1 = new RegisterRequestDto(
                "existinguser",
                "password123",
                "user1@example.com"
        );

        mockMvc.perform(post(BASE_URL + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        RegisterRequestDto user2 = new RegisterRequestDto(
                "existinguser",
                "password456",
                "user2@example.com"
        );

        mockMvc.perform(post(BASE_URL + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.username").value("Username is already taken"));
    }

    @Test
    void test_logout_with_valid_bearer_token_returns_ok() throws Exception {
        LoginRequestDto request = new LoginRequestDto("testuser", "password123");

        String responseBody = mockMvc.perform(post(BASE_URL + "/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TokenResponseDto tokenResponse = objectMapper.readValue(responseBody, TokenResponseDto.class);

        mockMvc.perform(post(BASE_URL + "/auth/logout")
                        .header("Authorization", "Bearer " + tokenResponse.token()))
                .andExpect(status().isOk());
    }

    @Test
    void test_missing_auth_header_returns_error() throws Exception {
        mockMvc.perform(post(BASE_URL + "/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_empty_token_returns_error() throws Exception {
        String emptyToken = "";

        mockMvc.perform(post(BASE_URL + "/auth/logout")
                        .header("Authorization", emptyToken))
                .andExpect(status().isForbidden());
    }

}