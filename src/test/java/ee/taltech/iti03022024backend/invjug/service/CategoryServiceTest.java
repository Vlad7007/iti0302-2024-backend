package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.CategoryMapper;
import ee.taltech.iti03022024backend.invjug.repository.CategoryRepository;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_category_with_valid_data_returns_mapped_dto() {
        // Given
        CategoryDto inputDto = new CategoryDto(null, "Electronics", "Electronics category");
        CategoryEntity unmappedEntity = new CategoryEntity();
        CategoryEntity savedEntity = new CategoryEntity();
        CategoryDto expectedDto = new CategoryDto(1L, "Electronics", "Electronics category");

        given(categoryMapper.toCategoryEntity(inputDto)).willReturn(unmappedEntity);
        given(categoryRepository.save(unmappedEntity)).willReturn(savedEntity);
        given(categoryMapper.toCategoryDto(savedEntity)).willReturn(expectedDto);

        // When
        CategoryDto result = categoryService.createCategory(inputDto);

        // Then
        assertThat(result).isEqualTo(expectedDto);
        verify(categoryMapper).toCategoryEntity(inputDto);
        verify(categoryRepository).save(unmappedEntity);
        verify(categoryMapper).toCategoryDto(savedEntity);
    }

    @Test
    void find_category_by_invalid_id_throws_not_found() {
        // Given
        Long nonExistentId = 999L;
        given(categoryRepository.findById(nonExistentId)).willReturn(Optional.empty());
        

        // When/Then
        assertThatThrownBy(() -> categoryService.findCategoryById(nonExistentId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(nonExistentId);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void test_create_category_database_save_failure() {
        // Given
        CategoryDto inputDto = new CategoryDto(null, "Electronics", "Electronics Category");
        CategoryEntity unmappedEntity = new CategoryEntity();

        given(categoryMapper.toCategoryEntity(inputDto)).willReturn(unmappedEntity);
        given(categoryRepository.save(unmappedEntity))
                .willThrow(new RuntimeException("Database save failed"));

        // When/Then
        assertThatThrownBy(() -> categoryService.createCategory(inputDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database save failed");

        verify(categoryMapper).toCategoryEntity(inputDto);
        verify(categoryRepository).save(unmappedEntity);
    }

    @Test
    void test_returns_all_categories_when_multiple_exist() {
        // Given
        CategoryEntity category1 = new CategoryEntity();
        category1.setId(1L);
        category1.setName("Electronics");

        CategoryEntity category2 = new CategoryEntity();
        category2.setId(2L);
        category2.setName("Books");

        List<CategoryEntity> categories = List.of(category1, category2);

        CategoryDto categoryDto1 = new CategoryDto(1L, "Electronics", "Electronics desc");
        CategoryDto categoryDto2 = new CategoryDto(2L, "Books", "Books desc");

        given(categoryRepository.findAll()).willReturn(categories);
        given(categoryMapper.toCategoryDto(category1)).willReturn(categoryDto1);
        given(categoryMapper.toCategoryDto(category2)).willReturn(categoryDto2);

        // When
        List<CategoryDto> result = categoryService.getAllCategories();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(categoryDto1, categoryDto2);
    }

    // Returns empty list when no categories exist in repository
    @Test
    void test_returns_empty_list_when_no_categories_exist() {
        // Given
        given(categoryRepository.findAll()).willReturn(Collections.emptyList());

        // When
        List<CategoryDto> result = categoryService.getAllCategories();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void test_returns_category_dto_for_valid_id() {
        // Given
        Long id = 1L;
        CategoryEntity categoryEntity = new CategoryEntity();
        CategoryDto expectedDto = new CategoryDto(id, "Electronics", "Electronics category");

        given(categoryRepository.findById(id)).willReturn(Optional.of(categoryEntity));
        given(categoryMapper.toCategoryDto(categoryEntity)).willReturn(expectedDto);

        // When
        CategoryDto actualDto = categoryService.findCategoryById(id);

        // Then
        assertThat(actualDto).isEqualTo(expectedDto);
        verify(categoryRepository).findById(id);
        verify(categoryMapper).toCategoryDto(categoryEntity);
    }

    // Throws NotFoundException when category ID does not exist
    @Test
    void test_throws_not_found_exception_for_invalid_id() {
        // Given
        Long id = 999L;
        given(categoryRepository.findById(id)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.findCategoryById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(id);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void test_update_category_success() {
        // Given
        Long categoryId = 1L;
        CategoryDto updatedDto = new CategoryDto(categoryId, "Updated Name", "Updated Description");
        CategoryEntity existingEntity = new CategoryEntity();
        CategoryEntity savedEntity = new CategoryEntity();
        CategoryDto expectedDto = new CategoryDto(categoryId, "Updated Name", "Updated Description");

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingEntity));
        given(categoryRepository.save(existingEntity)).willReturn(savedEntity);
        given(categoryMapper.toCategoryDto(savedEntity)).willReturn(expectedDto);

        // When
        CategoryDto result = categoryService.updateCategory(categoryId, updatedDto);

        // Then
        then(categoryMapper).should().updateEntityFromDto(updatedDto, existingEntity);
        then(categoryRepository).should().save(existingEntity);
        assertThat(result).isEqualTo(expectedDto);
    }

    // Throws NotFoundException when trying to update non-existent category ID
    @Test
    void test_update_category_not_found() {
        // Given
        Long nonExistentId = 999L;
        CategoryDto updatedDto = new CategoryDto(nonExistentId, "Name", "Description");

        given(categoryRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> categoryService.updateCategory(nonExistentId, updatedDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Category not found");

        then(categoryMapper).shouldHaveNoInteractions();
        then(categoryRepository).should(never()).save(any());
    }

    @Test
    void test_delete_existing_category_success() {
        // Given
        Long categoryId = 1L;
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        // When
        categoryService.deleteCategory(categoryId);

        // Then
        then(categoryRepository).should().delete(category);
    }

    // Attempt to delete category with non-existent ID throws NotFoundException
    @Test
    void test_delete_nonexistent_category_throws_exception() {
        // Given
        Long nonExistentId = 999L;
        given(categoryRepository.findById(nonExistentId)).willReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundException.class, () -> categoryService.deleteCategory(nonExistentId));
        then(categoryRepository).should(never()).delete(any());
    }
}
