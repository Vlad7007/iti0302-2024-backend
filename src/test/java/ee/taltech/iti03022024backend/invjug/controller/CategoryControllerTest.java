package ee.taltech.iti03022024backend.invjug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_create_category_as_manager_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Table",
                "le Table de Drop"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.description").value("le Table de Drop"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_create_category_as_admin_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Table",
                "Dropping Tables"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.description").value("Dropping Tables"));
    }
    @Test
    @WithMockUser(username = "user")
    void test_create_category_as_user_forbidden() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Table",
                "Dropping Tables"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_create_category_unauthenticated_forbidden() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Coffee Table",
                "Dropping Coffee Tables"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_category_with_null_fields() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                null,
                null
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name cannot be null"))
                .andExpect(jsonPath("$.description").value("Description cannot be null"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_category_with_empty_fields() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "",
                ""
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.description").value("Description must be between 2 and 255 characters"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_category_with_too_short_fields() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "e",
                "e"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.description").value("Description must be between 2 and 255 characters"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_category_with_too_long_fields() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "abobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaaboba",
                "abobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaaboba"
        );

        mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.description").value("Description must be between 2 and 255 characters"));
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_category_by_id_success() throws Exception {
        Long categoryId = 1L;

        mockMvc.perform(get(BASE_URL + "/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_category_by_id_not_found() throws Exception {
        long nonExistentCategoryId = 999L;

        mockMvc.perform(get(BASE_URL + "/categories/" + nonExistentCategoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_all_categories_success() throws Exception {
        mockMvc.perform(get(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_category_as_manager_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Table",
                "le Table de Drop"
        );

        CategoryDto createdCategory = createCategory(categoryDto);
        long categoryId = createdCategory.id();

        CategoryDto updatedCategoryDto = new CategoryDto(
                categoryId,
                "Table",
                "Dropping Tables"
        );

        String updatedResponse = mockMvc.perform(put(BASE_URL + "/categories/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Table"))
                .andExpect(jsonPath("$.description").value("Dropping Tables"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        deleteUpdatedCategory(updatedResponse);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_category_not_found() throws Exception {
        long nonExistentCategoryId = 999L;
        CategoryDto updatedCategoryDto = new CategoryDto(
                nonExistentCategoryId,
                "Ephemeral Table",
                "Dropping Ghost Tables"
        );

        mockMvc.perform(put(BASE_URL + "/categories/" + nonExistentCategoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategoryDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_category_as_manager_success() throws Exception {
        CategoryDto categoryDto = new CategoryDto(
                null,
                "Fading Table",
                "Dropping Disappearing Tables"
        );

        CategoryDto createdCategory = createCategory(categoryDto);
        long categoryId = createdCategory.id();

        mockMvc.perform(delete(BASE_URL + "/categories/" + categoryId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_category_not_found() throws Exception {
        long nonExistentCategoryId = 999L;

        mockMvc.perform(delete(BASE_URL + "/categories/" + nonExistentCategoryId))
                .andExpect(status().isNotFound());
    }

    private CategoryDto createCategory(CategoryDto categoryDto) throws Exception {
        String createdResponse = mockMvc.perform(post(BASE_URL + "/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createdResponse, CategoryDto.class);
    }

    private void deleteUpdatedCategory(String updatedCategoryResponse) throws Exception {
        CategoryDto updatedCategory = objectMapper.readValue(updatedCategoryResponse, CategoryDto.class);
        long updatedCategoryId = updatedCategory.id();

        mockMvc.perform(delete(BASE_URL + "/categories/" + updatedCategoryId))
                .andExpect(status().isNoContent());
    }

}
