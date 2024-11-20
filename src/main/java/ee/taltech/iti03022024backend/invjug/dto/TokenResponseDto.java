package ee.taltech.iti03022024backend.invjug.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token response data transfer object")
public record TokenResponseDto(
        @Schema(description = "Generated token for authentication",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        String token
) {}