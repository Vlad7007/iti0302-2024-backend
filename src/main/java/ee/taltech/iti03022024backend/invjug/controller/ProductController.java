package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/products")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
@Tag(name = "Products", description = "Products management APIs")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Add a new product to the system",
            description = "Creates a new product record based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Product added successfully")
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Received: {}", productDto);
        return productService.createProduct(productDto);
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Fetches a list of all products available in the system."
    )
    @ApiResponse(responseCode = "200", description = "Successfully received list of products")
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(
            summary = "Get a product by ID",
            description = "Fetches a product by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Successfully received the product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("{id}")
    public ProductDto getProduct(@PathVariable("id") Long id) {
        return productService.findProductById(id);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update an existing product",
            description = "Updates the details of an existing product by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ProductDto updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductDto updatedProductDto) {
        return productService.updateProduct(id, updatedProductDto);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its unique identifier."
    )
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }
}
