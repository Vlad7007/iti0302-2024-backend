package ee.taltech.iti03022024backend.invjug.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Login request data transfer object")
public record LoginRequestDto(
        @Schema(description = "Username to login with", example = "johnDoe")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        @NotNull(message = "Username cannot be null")
        String username,

        @Schema(description = "Password to login with", example = "password123")
        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        @NotNull(message = "Password cannot be null")
        String password
) {}