package ee.taltech.iti03022024backend.invjug.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.AbstractIntegrationTest;
import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    public void test_create_product_success() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Test Product",
                100L,
                10L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(100))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.categoryIds[0]").value(1))
                .andExpect(jsonPath("$.supplierId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_create_product_with_null_fields() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                null,
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name cannot be null"))
                .andExpect(jsonPath("$.price").value("Price cannot be null"))
                .andExpect(jsonPath("$.quantity").value("Quantity cannot be null"))
                .andExpect(jsonPath("$.categoryIds").value("Category IDs cannot be null"))
                .andExpect(jsonPath("$.supplierId").value("Supplier ID cannot be null"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void test_create_product_with_negative_values() throws Exception {

        @SuppressWarnings("DataFlowIssue")
        ProductDto productDto = new ProductDto(
                null,
                "Test Product",
                -100L,
                -10L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantity").value("Quantity must be greater than or equal to 0"))
                .andExpect(jsonPath("$.price").value("Price must be greater than or equal to 0"));

    }

    @Test
    void getProducts() {
    }

    @Test
    void getProduct() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }
}