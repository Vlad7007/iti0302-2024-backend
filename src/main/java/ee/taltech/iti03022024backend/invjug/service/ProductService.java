package ee.taltech.iti03022024backend.invjug.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.errorhandling.ProductServiceException;
import ee.taltech.iti03022024backend.invjug.mapping.ProductMapper;
import ee.taltech.iti03022024backend.invjug.entities.ProductEntity;
import ee.taltech.iti03022024backend.invjug.repository.CategoryRepository;
import ee.taltech.iti03022024backend.invjug.repository.ProductRepository;
import ee.taltech.iti03022024backend.invjug.specifications.PageResponse;
import ee.taltech.iti03022024backend.invjug.specifications.ProductSearchCriteria;
import ee.taltech.iti03022024backend.invjug.specifications.ProductSpecifications;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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

    public PageResponse<ProductDto> findProducts(ProductSearchCriteria criteria) throws ProductServiceException {
        Specification<ProductEntity> spec = Specification.where(null);

        if (criteria.name() != null) {
            spec = spec.and(ProductSpecifications.nameContains(criteria.name()));
        }
        if (criteria.minPrice() != null || criteria.maxPrice() != null) {
            spec = spec.and(ProductSpecifications.priceInRange(criteria.minPrice(), criteria.maxPrice()));
        }

        String sortBy = criteria.sortBy() != null ? criteria.sortBy() : "id";
        String direction = criteria.sortDirection() != null ? criteria.sortDirection().toUpperCase() : "DESC";
        int pageNumber = criteria.page() != null ? criteria.page() : 0;
        int pageSize = criteria.size() != null ? criteria.size() : 20;

        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);


        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = null;
        try {
            jsonContent = objectMapper.writeValueAsString(productPage.map(productMapper::toProductDto).getContent());
        } catch (JsonProcessingException e) {
            throw new ProductServiceException(e.getMessage());
        }
        log.info("Raw page content in JSON:\n {}", jsonContent);


        return PageResponse.of(productPage.map(productMapper::toProductDto));
    }

    private void validateCategoryIds(List<Long> categoryIds) {
        List<CategoryEntity> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException(CATEGORY_MESSAGE);
        }
    }
}
