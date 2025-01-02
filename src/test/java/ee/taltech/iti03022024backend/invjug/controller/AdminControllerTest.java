package ee.taltech.iti03022024backend.invjug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.invjug.dto.RegisterRequestDto;
import ee.taltech.iti03022024backend.invjug.dto.UserDto;
import ee.taltech.iti03022024backend.invjug.entities.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_get_all_users_success_as_admin() throws Exception {
        mockMvc.perform(get(BASE_URL + "/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].role").exists());
    }

    @Test
    @WithMockUser(username = "testmanager", roles = {"MANAGER"})
    void test_get_all_users_forbidden_as_manager() throws Exception {
        mockMvc.perform(get(BASE_URL + "/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void test_get_all_users_forbidden_as_user() throws Exception {
        mockMvc.perform(get(BASE_URL + "/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_update_user_success_as_admin() throws Exception {
        Long userId = createTestUser();
        UserDto userDto = new UserDto(userId, "updatedWithAdminUser", "updated@example.com", Role.ROLE_USER);

        mockMvc.perform(put(BASE_URL + "/admin/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedWithAdminUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @WithMockUser(username = "testmanager", roles = {"MANAGER"})
    void test_update_user_forbidden_as_manager() throws Exception {
        UserDto userDto = new UserDto(1L, "updatedWithManagerUser", "updated@example.com", Role.ROLE_USER);

        mockMvc.perform(put(BASE_URL + "/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser")
    void test_update_user_forbidden_as_user() throws Exception {
        UserDto userDto = new UserDto(1L, "updatedWithUserUser", "updated@example.com", Role.ROLE_USER);

        mockMvc.perform(put(BASE_URL + "/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_get_user_by_id_success_as_admin() throws Exception {
        mockMvc.perform(get(BASE_URL + "/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").exists());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_get_user_by_id_not_found() throws Exception {
        mockMvc.perform(get(BASE_URL + "/admin/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_delete_user_success() throws Exception {
        Long userId = createTestUser();

        mockMvc.perform(delete(BASE_URL + "/admin/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = {"ADMIN"})
    void test_delete_user_not_found() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/admin/users/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private Long createTestUser() throws Exception {
        RegisterRequestDto request = new RegisterRequestDto("testUserToDelete", "password123", "testUserToDelete@example.com");

        mockMvc.perform(post(BASE_URL + "/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        String response = mockMvc.perform(get(BASE_URL + "/admin/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> users = objectMapper.readValue(response, new TypeReference<>() {});
        return users.stream()
                .filter(user -> "testUserToDelete".equals(user.username()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"))
                .id();
    }
}