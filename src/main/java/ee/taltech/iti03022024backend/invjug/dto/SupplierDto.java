package ee.taltech.iti03022024backend.invjug.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Data transfer object for supplier information")

public record SupplierDto(

        @Schema(description = "Unique identifier of the supplier", example = "1")
        Long id,

        @Schema(description = "Name of the supplier", example = "John Doe")
        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
        String name,

        @Schema(description = "Email address of the supplier", example = "john.doe@example.com")
        @NotNull(message = "Email cannot be null")
        @Size(min = 2, max = 255, message = "Email must be between 2 and 255 characters")
        @Email(message = "Email must match the format user@example.com")
        String email
) {}
