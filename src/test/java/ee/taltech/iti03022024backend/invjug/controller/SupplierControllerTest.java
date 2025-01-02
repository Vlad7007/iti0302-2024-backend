package ee.taltech.iti03022024backend.invjug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.invjug.dto.SupplierDto;
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
class SupplierControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_create_supplier_as_manager_success() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Johnathan Drop Tables",
                "johnathan@drop.tables"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Johnathan Drop Tables"))
                .andExpect(jsonPath("$.email").value("johnathan@drop.tables"));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_create_supplier_as_admin_success() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Jotaro Drop Tables",
                "jotaro@drop.tables"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jotaro Drop Tables"))
                .andExpect(jsonPath("$.email").value("jotaro@drop.tables"));
    }
    @Test
    @WithMockUser(username = "user")
    void test_create_supplier_as_user_forbidden() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Johnathan Drop Tables",
                "johnathan@drop.tables"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_create_supplier_unauthenticated_forbidden() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Jotaro Drop Tables",
                "jotaro@drop.tables"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_supplier_with_null_fields() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                null,
                null
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name cannot be null"))
                .andExpect(jsonPath("$.email").value("Email cannot be null"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_supplier_with_empty_fields() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "",
                ""
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_supplier_with_too_short_fields() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "j",
                "j"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_supplier_with_too_long_fields() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "abobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaaboba",
                "abobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaabobaaboba"
        );

        mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must be between 2 and 255 characters"))
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_supplier_by_id_success() throws Exception {
        Long supplierId = 1L;

        mockMvc.perform(get(BASE_URL + "/suppliers/" + supplierId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(supplierId))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_supplier_by_id_not_found() throws Exception {
        long nonExistentSupplierId = 999L;

        mockMvc.perform(get(BASE_URL + "/suppliers/" + nonExistentSupplierId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Supplier not found"));
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_all_suppliers_success() throws Exception {
        mockMvc.perform(get(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_supplier_as_manager_success() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Johnathan Drop Tables",
                "johnathan@drop.tables"
        );

        SupplierDto createdSupplier = createSupplier(supplierDto);
        long supplierId = createdSupplier.id();

        SupplierDto updatedSupplierDto = new SupplierDto(
                supplierId,
                "Jotaro Drop Tables",
                "jotaro@drop.tables"
        );

        String updatedResponse = mockMvc.perform(put(BASE_URL + "/suppliers/" + supplierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSupplierDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jotaro Drop Tables"))
                .andExpect(jsonPath("$.email").value("jotaro@drop.tables"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        deleteUpdatedSupplier(updatedResponse);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_supplier_not_found() throws Exception {
        long nonExistentSupplierId = 999L;
        SupplierDto updatedSupplierDto = new SupplierDto(
                nonExistentSupplierId,
                "Johnathan Drop Tables",
                "johnathan@drop.tables"
        );

        mockMvc.perform(put(BASE_URL + "/suppliers/" + nonExistentSupplierId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSupplierDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Supplier not found"));
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_supplier_as_manager_success() throws Exception {
        SupplierDto supplierDto = new SupplierDto(
                null,
                "Johnathan Drop Tables",
                "johnathan@drop.tables"
        );

        SupplierDto createdSupplier = createSupplier(supplierDto);
        long supplierId = createdSupplier.id();

        mockMvc.perform(delete(BASE_URL + "/suppliers/" + supplierId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_supplier_not_found() throws Exception {
        long nonExistentSupplierId = 999L;

        mockMvc.perform(delete(BASE_URL + "/suppliers/" + nonExistentSupplierId))
                .andExpect(status().isNotFound());
    }

    private SupplierDto createSupplier(SupplierDto supplierDto) throws Exception {
        String createdResponse = mockMvc.perform(post(BASE_URL + "/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplierDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createdResponse, SupplierDto.class);
    }

    private void deleteUpdatedSupplier(String updatedSupplierResponse) throws Exception {
        SupplierDto updatedSupplier = objectMapper.readValue(updatedSupplierResponse, SupplierDto.class);
        long updatedSupplierId = updatedSupplier.id();

        mockMvc.perform(delete(BASE_URL + "/suppliers/" + updatedSupplierId))
                .andExpect(status().isNoContent());
    }

}
