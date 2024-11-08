package ee.taltech.iti03022024backend.invjug.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryDto(
        Long id,

        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        String description
) {}
