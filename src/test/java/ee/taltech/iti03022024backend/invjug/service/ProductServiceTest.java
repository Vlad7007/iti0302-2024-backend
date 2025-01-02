package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.ProductDto;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.entities.ProductEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.ProductMapper;
import ee.taltech.iti03022024backend.invjug.repository.CategoryRepository;
import ee.taltech.iti03022024backend.invjug.repository.ProductRepository;
import ee.taltech.iti03022024backend.invjug.repository.SupplierRepository;
import ee.taltech.iti03022024backend.invjug.specifications.PageResponse;
import ee.taltech.iti03022024backend.invjug.specifications.ProductSearchCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Spy
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    void test_create_product_with_valid_data() {
        ProductDto inputDto = new ProductDto(null, "Valid Product", 100L, 10L, List.of(1L, 2L), 1L);
        List<CategoryEntity> categories = List.of(new CategoryEntity(), new CategoryEntity());
        given(categoryRepository.findAllById(inputDto.categoryIds())).willReturn(categories);
        given(supplierRepository.existsById(inputDto.supplierId())).willReturn(true);

        ProductEntity productEntity = new ProductEntity();
        given(productMapper.toProductEntity(inputDto)).willReturn(productEntity);
        given(productRepository.save(productEntity)).willReturn(productEntity);
        given(productMapper.toProductDto(productEntity)).willReturn(inputDto);

        ProductDto result = productService.createProduct(inputDto);

        assertThat(result).isEqualTo(inputDto);
        then(categoryRepository).should(times(2)).findAllById(inputDto.categoryIds());
        then(supplierRepository).should().existsById(inputDto.supplierId());
        then(productMapper).should().toProductEntity(inputDto);
        then(productRepository).should().save(productEntity);
        then(productMapper).should().toProductDto(productEntity);
    }

    @Test
    void test_create_product_with_invalid_category_ids() {
        ProductDto inputDto = new ProductDto(null, "Test Product", 100L, 10L, List.of(1L, 2L), 1L);
        List<CategoryEntity> categories = List.of(new CategoryEntity());

        given(categoryRepository.findAllById(inputDto.categoryIds())).willReturn(categories);

        assertThatThrownBy(() -> productService.createProduct(inputDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("One or more categories not found");

        then(categoryRepository).should().findAllById(inputDto.categoryIds());
        then(supplierRepository).shouldHaveNoInteractions();
        then(productMapper).shouldHaveNoInteractions();
        then(productRepository).shouldHaveNoInteractions();
    }

    @Test
    void test_create_product_with_invalid_supplier_id() {
        ProductDto inputDto = new ProductDto(null, "Test Product", 100L, 10L, List.of(1L, 2L), 999L);
        List<CategoryEntity> categories = List.of(new CategoryEntity(), new CategoryEntity());

        given(categoryRepository.findAllById(inputDto.categoryIds())).willReturn(categories);
        given(supplierRepository.existsById(inputDto.supplierId())).willReturn(false);

        assertThatThrownBy(() -> productService.createProduct(inputDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("One or more suppliers not found");

        then(categoryRepository).should().findAllById(inputDto.categoryIds());
        then(supplierRepository).should().existsById(inputDto.supplierId());
        then(productMapper).shouldHaveNoInteractions();
        then(productRepository).shouldHaveNoInteractions();
    }

    @Test
    void test_find_product_by_valid_id_returns_mapped_dto() {
        Long productId = 1L;
        ProductEntity productEntity = new ProductEntity();
        ProductDto expectedDto = new ProductDto(1L, "Test Product", 100L, 5L, List.of(1L), 1L);

        given(productRepository.findById(productId)).willReturn(Optional.of(productEntity));
        given(productMapper.toProductDto(productEntity)).willReturn(expectedDto);

        ProductDto actualDto = productService.findProductById(productId);

        assertThat(actualDto).isEqualTo(expectedDto);
        verify(productRepository).findById(productId);
        verify(productMapper).toProductDto(productEntity);
    }

    @Test
    void test_find_product_by_nonexistent_id_throws_not_found() {
        Long nonExistentId = 999L;
        given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findProductById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Product not found");

        verify(productRepository).findById(nonExistentId);
        verifyNoInteractions(productMapper);
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void test_find_product_by_null_id_throws_exception() {
        given(productRepository.findById(null)).willThrow(IllegalArgumentException.class);

        assertThatThrownBy(() -> productService.findProductById(null))
                .isInstanceOf(IllegalArgumentException.class);

        verify(productRepository).findById(null);
        verifyNoInteractions(productMapper);
    }

    @Test
    void test_update_product_success() {
        Long productId = 1L;
        List<Long> categoryIds = List.of(1L, 2L);
        ProductDto updatedDto = new ProductDto(1L, "Updated Jug", 100L, 5L, categoryIds, 1L);
        ProductEntity existingProduct = new ProductEntity();
        ProductEntity updatedProduct = new ProductEntity();
        List<CategoryEntity> categories = List.of(new CategoryEntity(), new CategoryEntity());

        given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
        given(supplierRepository.existsById(1L)).willReturn(true);
        given(categoryRepository.findAllById(categoryIds)).willReturn(categories);
        given(productRepository.save(existingProduct)).willReturn(updatedProduct);
        given(productMapper.toProductDto(updatedProduct)).willReturn(updatedDto);

        ProductDto result = productService.updateProduct(productId, updatedDto);

        assertThat(result).isEqualTo(updatedDto);
        verify(productMapper).updateEntityFromDto(updatedDto, existingProduct);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void test_update_product_not_found() {
        Long nonExistentId = 999L;
        List<Long> categoryIds = List.of(1L);
        ProductDto updatedDto = new ProductDto(nonExistentId, "Test Jug", 50L, 1L, categoryIds, 1L);

        given(productRepository.findById(nonExistentId)).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                productService.updateProduct(nonExistentId, updatedDto));

        verify(productRepository).findById(nonExistentId);
        verifyNoMoreInteractions(productRepository, categoryRepository, supplierRepository, productMapper);
    }

    @Test
    void test_update_product_invalid_categories() {
        Long productId = 1L;
        List<Long> categoryIds = List.of(1L, 2L);
        ProductDto updatedDto = new ProductDto(productId, "Test Jug", 50L, 1L, categoryIds, 1L);
        ProductEntity existingProduct = new ProductEntity();
        List<CategoryEntity> foundCategories = List.of(new CategoryEntity());

        given(productRepository.findById(productId)).willReturn(Optional.of(existingProduct));
        given(categoryRepository.findAllById(categoryIds)).willReturn(foundCategories);

        assertThrows(NotFoundException.class, () ->
                productService.updateProduct(productId, updatedDto));

        verify(productRepository).findById(productId);
        verify(categoryRepository).findAllById(categoryIds);
    }

    @Test
    void test_delete_existing_product() {
        Long productId = 1L;

        given(productRepository.existsById(productId)).willReturn(true);

        productService.deleteProduct(productId);

        then(productRepository).should().deleteById(productId);
        then(productRepository).should().existsById(productId);
    }

    @Test
    void deleteProduct_ThrowsNotFoundException() {
        Long productId = 999L;
        given(productRepository.existsById(productId)).willReturn(false);

        assertThrows(NotFoundException.class, () -> productService.deleteProduct(productId));
        then(productRepository).should().existsById(productId);
        then(productRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    public void test_search_by_name_returns_matching_products() {
        ProductSearchCriteria criteria = new ProductSearchCriteria("apple", null, null, null, null, null, null);

        ProductEntity product1 = new ProductEntity();
        product1.setName("Apple iPhone");

        ProductEntity product2 = new ProductEntity();
        product2.setName("APPLE Watch");

        setupProductMocks(List.of(product1, product2));

        PageResponse<ProductDto> result = productService.findProducts(criteria);

        assertThat(result.content()).hasSize(2);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void test_search_with_null_criteria_returns_all_products() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, null, null, null, null, null);

        List<ProductEntity> products = List.of(
                new ProductEntity(),
                new ProductEntity()
        );

        setupProductMocks(products);

        PageResponse<ProductDto> result = productService.findProducts(criteria);

        assertThat(result.content()).hasSize(2);
        verify(productRepository).findAll(any(Specification.class),
                argThat((Pageable p) -> p.getPageSize() == 20 && p.getPageNumber() == 0));
    }

    @Test
    void test_search_with_empty_name_returns_all_products() {
        ProductSearchCriteria criteria = new ProductSearchCriteria("", null, null, null, null, null, null);

        List<ProductEntity> products = List.of(
                new ProductEntity(),
                new ProductEntity(),
                new ProductEntity()
        );

        setupProductMocks(products);

        PageResponse<ProductDto> result = productService.findProducts(criteria);

        assertThat(result.content()).hasSize(3);
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void test_find_products_with_pagination() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, null, 2, 5, null, null);

        List<ProductEntity> products = List.of(
                new ProductEntity(),
                new ProductEntity(),
                new ProductEntity(),
                new ProductEntity(),
                new ProductEntity()
        );

        setupProductMocks(products);

        PageResponse<ProductDto> result = productService.findProducts(criteria);

        assertThat(result.content()).hasSize(5);
        verify(productRepository).findAll(any(Specification.class),
                argThat((Pageable p) -> p.getPageSize() == 5 && p.getPageNumber() == 1));
    }

    @Test
    void test_find_products_with_sorting_and_filtering() {
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, new BigDecimal("50"), new BigDecimal("150"), null, null, "name", "ASC");

        ProductEntity product1 = new ProductEntity();
        product1.setName("Apple iPhone");
        product1.setPrice(120L);

        ProductEntity product2 = new ProductEntity();
        product2.setName("Banana Phone");
        product2.setPrice(100L);

        ProductEntity product3 = new ProductEntity();
        product3.setName("Cherry Tablet");
        product3.setPrice(80L);

        setupProductMocks(List.of(product1, product2, product3));

        PageResponse<ProductDto> result = productService.findProducts(criteria);

        assertThat(result.content()).hasSize(3);
        assertThat(result.content().get(0).name()).isEqualTo("Apple iPhone");
        assertThat(result.content().get(1).name()).isEqualTo("Banana Phone");
        assertThat(result.content().get(2).name()).isEqualTo("Cherry Tablet");

        verify(productRepository).findAll(any(Specification.class),
                argThat((Pageable p) -> {
                    Sort.Order order = p.getSort().getOrderFor("name");
                    return order != null && order.getDirection().isAscending();
                }));
    }

    private void setupProductMocks(List<ProductEntity> productEntities) {
        Page<ProductEntity> page = new PageImpl<>(productEntities);

        given(productRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(page);
        given(productMapper.toProductDto(any(ProductEntity.class)))
                .willAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    return new ProductDto(entity.getId(), entity.getName(), entity.getPrice(), 5L, List.of(1L), 1L);
                });
    }
}