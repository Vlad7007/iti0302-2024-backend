package ee.taltech.iti03022024backend.invjug.dto;

import ee.taltech.iti03022024backend.invjug.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDto(
        Long id,

        @NotNull(message = "Username cannot be null")
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        String username,

        @NotNull(message = "Email cannot be null")
        @Email(message = "Email must match the format user@example.com")
        @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
        String email,

        @NotNull(message = "Role cannot be null")
        Role role
) {
}
