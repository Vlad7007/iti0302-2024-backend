package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/products")
@RestController
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("{id}")
    public ProductDto getProduct(@PathVariable("id") Long id) {
        return productService.findProductById(id);
    }

    @PutMapping("{id}")
    public ProductDto updateProduct(@PathVariable("id") Long id, @Valid @RequestBody ProductDto updatedProductDto) {
        return productService.updateProduct(id, updatedProductDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
    }
}
