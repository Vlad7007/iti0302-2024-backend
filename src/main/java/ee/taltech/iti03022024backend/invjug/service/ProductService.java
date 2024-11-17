package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.mapping.ProductMapper;
import ee.taltech.iti03022024backend.invjug.entities.ProductEntity;
import ee.taltech.iti03022024backend.invjug.repository.CategoryRepository;
import ee.taltech.iti03022024backend.invjug.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private static final String PRODUCT_MESSAGE = "Product not found";
    private static final String CATEGORY_MESSAGE = "One or more categories not found";

    public ProductDto createProduct(ProductDto productDto) {
        validateCategoryIds(productDto.categoryIds());
        ProductEntity productEntity = productMapper.toProductEntity(productDto);
        List<CategoryEntity> categories = categoryRepository.findAllById(productDto.categoryIds());
        productEntity.setCategories(categories);
        ProductEntity savedEntity = productRepository.save(productEntity);
        return productMapper.toProductDto(savedEntity);
    }


    public List<ProductDto> getAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream().map(productMapper::toProductDto).toList();
    }


    public ProductDto findProductById(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_MESSAGE));
        return productMapper.toProductDto(productEntity);
    }

    public ProductDto updateProduct(Long id, ProductDto updatedProductDto) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_MESSAGE));

        validateCategoryIds(updatedProductDto.categoryIds());
        productMapper.updateEntityFromDto(updatedProductDto, productEntity);
        List<CategoryEntity> categories = categoryRepository.findAllById(updatedProductDto.categoryIds());
        productEntity.setCategories(categories);

        ProductEntity updatedEntity = productRepository.save(productEntity);
        return productMapper.toProductDto(updatedEntity);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException(PRODUCT_MESSAGE);
        }
        productRepository.deleteById(id);
    }

    private void validateCategoryIds(List<Long> categoryIds) {
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException(CATEGORY_MESSAGE);
        }
    }
}
