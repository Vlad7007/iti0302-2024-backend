package ee.taltech.iti03022024backend.invjug.dto;

import ee.taltech.iti03022024backend.invjug.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "User data transfer object")
public record UserDto(
        @Schema(description = "Unique identifier for the user", example = "1")
        Long id,

        @Schema(description = "Username chosen by the user", example = "johnDoe")
        @NotNull(message = "Username cannot be null")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        String username,

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        @NotNull(message = "Email cannot be null")
        @Email(message = "Email must match the format user@example.com")
        @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
        String email,

        @Schema(description = "Role of the user (USER,MANAGER, ADMIN)", example = "USER")
        @NotNull(message = "Role cannot be null")
        Role role
) {}
