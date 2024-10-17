package ee.taltech.iti03022024backend.invjug.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductDto(
        Long id,

        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @NotNull(message = "Price cannot be null")
        @Min(value = 0, message = "Price must be greater than or equal to 0")
        Long price,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 0, message = "Quantity must be greater than or equal to 0")
        Long quantity,

        @NotNull(message = "Category ID cannot be null")
        Long categoryId,

        @NotNull(message = "Supplier ID cannot be null")
        Long supplierId
) {}
