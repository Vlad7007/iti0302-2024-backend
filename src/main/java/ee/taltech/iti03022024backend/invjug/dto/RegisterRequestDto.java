package ee.taltech.iti03022024backend.invjug.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Registration request data transfer object")
public record RegisterRequestDto(
        @Schema(description = "Username to register with", example = "johnDoe")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        @NotNull(message = "Username cannot be null")
        String username,

        @Schema(description = "Password to register with", example = "password123")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        @NotNull(message = "Password cannot be null")
        String password,

        @Schema(description = "Email address to register with", example = "john.doe@example.com")
        @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
        @Email(message = "Email must match the format user@example.com")
        @NotNull(message = "Email cannot be null")
        String email
) {
}