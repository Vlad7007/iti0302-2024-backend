package ee.taltech.iti03022024backend.invjug.specifications;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Search criteria for products")
public record ProductSearchCriteria(
        @Schema(description = "Name of the product to search for", example = "Apple")
        @Size(max = 255, message = "Name must be less than 255 characters")
        String name,

        @Schema(description = "Minimum price of the product to search for", example = "10.99")
        @PositiveOrZero(message = "Minimum price must be zero or positive")
        BigDecimal minPrice,

        @Schema(description = "Maximum price of the product to search for", example = "99.99")
        @PositiveOrZero(message = "Maximum price must be zero or positive")
        BigDecimal maxPrice,

        @Schema(description = "Page number for pagination", example = "1")
        @Min(value = 0, message = "Page number must be zero or greater")
        Integer page,

        @Schema(description = "Page size for pagination", example = "10")
        @Min(value = 1, message = "Page size must be at least 1")
        @Max(value = 100, message = "Page size must be less than or equal to 100")
        Integer size,

        @Schema(description = "Field to sort by", example = "name")
        @Pattern(regexp = "id|name|price|quantity", message = "Sort by must be one of: id, name, price, quantity")
        String sortBy,

        @Schema(description = "Sort direction", example = "ASC")
        @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Sort direction must be either ASC or DESC")
        String sortDirection
) {}