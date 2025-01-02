package ee.taltech.iti03022024backend.invjug.service;

import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import ee.taltech.iti03022024backend.invjug.entities.CategoryEntity;
import ee.taltech.iti03022024backend.invjug.errorhandling.NotFoundException;
import ee.taltech.iti03022024backend.invjug.mapping.CategoryMapper;
import ee.taltech.iti03022024backend.invjug.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    private static final String CATEGORY_MESSAGE = "Category not found";


    public CategoryDto createCategory(CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(categoryDto);
        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);

        return categoryMapper.toCategoryDto(savedEntity);
    }

    public List<CategoryDto> getAllCategories() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream().map(categoryMapper::toCategoryDto).toList();
    }

    public CategoryDto findCategoryById(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_MESSAGE));
        return categoryMapper.toCategoryDto(categoryEntity);
    }

    public CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_MESSAGE));

        categoryMapper.updateEntityFromDto(updatedCategoryDto, categoryEntity);
        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);

        return categoryMapper.toCategoryDto(savedEntity);
    }

    public void deleteCategory(Long id) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CATEGORY_MESSAGE));

        categoryRepository.delete(categoryEntity);
    }
}
