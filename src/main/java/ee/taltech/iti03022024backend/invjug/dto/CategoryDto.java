package ee.taltech.iti03022024backend.invjug.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Data transfer object for category information")
public record CategoryDto(

        @Schema(description = "Unique identifier for the category", example = "1")
        Long id,

        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        @Schema(description = "Name of the category", example = "Electronics")
        String name,

        @NotNull(message = "Description cannot be null")
        @Size(min = 2, max = 255, message = "Description must be between 2 and 255 characters")
        @Schema(description = "Description of the category", example = "Electronics category")
        String description
) {}
