package ee.taltech.iti03022024backend.invjug.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

@Schema(description = "Data Transfer Object for Product information")
public record ProductDto(
        @Schema(description = "Id of the product", example = "2")
        Long id,

        @Schema(description = "Name of the product", example = "Red Jug")
        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @Schema(description = "Price of the product", example = "55")
        @NotNull(message = "Price cannot be null")
        @PositiveOrZero(message = "Price must be greater than or equal to 0")
        Long price,

        @Schema(description = "Quantity of the product", example = "2")
        @NotNull(message = "Quantity cannot be null")
        @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
        Long quantity,

        @Schema(description = "List of category IDs associated with the product", example = "[1, 2, 3]")
        @NotNull(message = "Category IDs cannot be null")
        @NotEmpty(message = "Category IDs cannot be empty")
        List<@Min(value = 1, message = "Category ID must be greater than 0") Long> categoryIds,

        @Schema(description = "Supplier ID associated with the product", example = "3")
        @NotNull(message = "Supplier ID cannot be null")
        @Min(value = 1, message = "Supplier ID must be greater than 0")
        Long supplierId
) {}
