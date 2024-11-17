package ee.taltech.iti03022024backend.invjug.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @Size(min = 5, max = 255, message = "Username must be between 5 and 255 characters")
        @NotNull(message = "Username cannot be null")
        String username,

        @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
        @NotNull(message = "Password cannot be null")
        String password
) {}
