package ee.taltech.iti03022024backend.invjug.controller;

import ee.taltech.iti03022024backend.invjug.dto.CategoryDto;
import ee.taltech.iti03022024backend.invjug.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequestMapping("api/categories")
@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
@RestController
@RequiredArgsConstructor
@Slf4j
@Transactional
@Tag(name = "Categories", description = "Category management APIs")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(
            summary = "Add a new category to the system",
            description = "Creates a new category record based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Category added successfully")
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Received: {}", categoryDto);
        return categoryService.createCategory(categoryDto);
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Fetches all the categories."
    )
    @ApiResponse(responseCode = "200", description = "Successfully received list of categories")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Operation(
            summary = "Get a category by ID",
            description = "Fetches a category by its unique identifier."
    )
    @ApiResponse(responseCode = "200", description = "Successfully received the category")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("{id}")
    public CategoryDto getCategory(@PathVariable("id") Long id) {
        return categoryService.findCategoryById(id);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update an existing category",
            description = "Updates the details of an existing category by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    public CategoryDto updateCategory(@PathVariable("id") Long id, @Valid @RequestBody CategoryDto updatedCategoryDto) {
        return categoryService.updateCategory(id, updatedCategoryDto);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete a category",
            description = "Deletes a category by its unique identifier."
    )
    @ApiResponse(responseCode = "204", description = "Category deleted successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
    }
}
