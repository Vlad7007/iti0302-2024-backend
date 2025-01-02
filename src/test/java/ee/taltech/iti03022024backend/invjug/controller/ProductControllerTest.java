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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // createProduct()
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_create_product_as_manager_success() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Manager Product",
                150L,
                20L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Manager Product"))
                .andExpect(jsonPath("$.price").value(150))
                .andExpect(jsonPath("$.quantity").value(20))
                .andExpect(jsonPath("$.categoryIds[0]").value(1))
                .andExpect(jsonPath("$.supplierId").value(1));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void test_create_product_as_admin_success() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Admin Product",
                200L,
                30L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin Product"))
                .andExpect(jsonPath("$.price").value(200))
                .andExpect(jsonPath("$.quantity").value(30))
                .andExpect(jsonPath("$.categoryIds[0]").value(1))
                .andExpect(jsonPath("$.supplierId").value(1));
    }

    @Test
    @WithMockUser(username = "user")
    void test_create_product_as_user_forbidden() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "User Product",
                100L,
                10L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void test_create_product_unauthenticated_forbidden() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Unauthenticated Product",
                100L,
                10L,
                List.of(1L),
                1L
        );

        mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_product_with_null_fields() throws Exception {
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
                .andExpect(jsonPath("$.categoryIds").value("Category IDs cannot be empty"))
                .andExpect(jsonPath("$.supplierId").value("Supplier ID cannot be null"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test_create_product_with_negative_values() throws Exception {

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

    // getProducts()
    @Test
    @WithMockUser(username = "user")
    void test_get_products_by_name() throws Exception {
        mockMvc.perform(get(BASE_URL + "/products")
                        .param("name", "Smart TV")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Smart TV"));
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_products_by_price_range() throws Exception {
        mockMvc.perform(get(BASE_URL + "/products")
                        .param("minPrice", "500")
                        .param("maxPrice", "800")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].price").value(containsInAnyOrder(500, 700, 800)));
    }


    @Test
    @WithMockUser(username = "user")
    void test_get_products_with_pagination() throws Exception {
        mockMvc.perform(get(BASE_URL + "/products")
                        .param("page", "1")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(1));
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_products_with_sorting() throws Exception {
        mockMvc.perform(get(BASE_URL + "/products")
                        .param("sortBy", "price")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].price").value(contains(150, 500, 700, 800, 1200)));
    }

    // getProduct()
    @Test
    @WithMockUser(username = "user")
    void test_get_product_by_id_success() throws Exception {
        Long productId = 1L;

        mockMvc.perform(get(BASE_URL + "/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.price").isNotEmpty())
                .andExpect(jsonPath("$.quantity").isNotEmpty())
                .andExpect(jsonPath("$.categoryIds").isArray())
                .andExpect(jsonPath("$.supplierId").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "user")
    void test_get_product_by_id_not_found() throws Exception {
        long nonExistentProductId = 999L;

        mockMvc.perform(get(BASE_URL + "/products/" + nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }


    // updateProduct()
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_product_as_manager_success() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Product to Update",
                100L,
                10L,
                List.of(1L),
                1L
        );

        ProductDto createdProduct = createProduct(productDto);
        Long productId = createdProduct.id();

        ProductDto updatedProductDto = new ProductDto(
                productId,
                "Updated Product",
                200L,
                15L,
                List.of(1L),
                1L
        );

        String updatedResponse = mockMvc.perform(put(BASE_URL + "/products/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(200))
                .andExpect(jsonPath("$.quantity").value(15))
                .andExpect(jsonPath("$.categoryIds[0]").value(1))
                .andExpect(jsonPath("$.supplierId").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        deleteUpdatedProduct(updatedResponse);
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_update_product_not_found() throws Exception {
        Long nonExistentProductId = 999L;
        ProductDto updatedProductDto = new ProductDto(
                nonExistentProductId,
                "Non-existent Product",
                200L,
                15L,
                List.of(1L),
                1L
        );

        mockMvc.perform(put(BASE_URL + "/products/" + nonExistentProductId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProductDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    // deleteProduct()
    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_product_as_manager_success() throws Exception {
        ProductDto productDto = new ProductDto(
                null,
                "Product to Delete",
                100L,
                10L,
                List.of(1L),
                1L
        );

        ProductDto createdProduct = createProduct(productDto);
        Long productId = createdProduct.id();

        mockMvc.perform(delete(BASE_URL + "/products/" + productId))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    void test_delete_product_not_found() throws Exception {
        long nonExistentProductId = 999L;

        mockMvc.perform(delete(BASE_URL + "/products/" + nonExistentProductId))
                .andExpect(status().isNotFound());
    }

    private ProductDto createProduct(ProductDto productDto) throws Exception {
        String createdResponse = mockMvc.perform(post(BASE_URL + "/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(createdResponse, ProductDto.class);
    }

    private void deleteUpdatedProduct(String updatedProductResponse) throws Exception {
        ProductDto updatedProduct = objectMapper.readValue(updatedProductResponse, ProductDto.class);
        Long updatedProductId = updatedProduct.id();

        mockMvc.perform(delete(BASE_URL + "/products/" + updatedProductId))
                .andExpect(status().isNoContent());
    }
}