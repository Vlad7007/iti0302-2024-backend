package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import ee.taltech.iti03022024backend.invjug.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/categories")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
}
