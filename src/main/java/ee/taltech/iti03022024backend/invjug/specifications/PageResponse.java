package ee.taltech.iti03022024backend.invjug.specifications;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Response containing a page of data")
public record PageResponse<T>(
        @Schema(description = "List of items in the page")
        List<T> content,

        @Schema(description = "Current page number", example = "1")
        int number,

        @Schema(description = "Size of the page", example = "10")
        int size,

        @Schema(description = "Total number of elements", example = "50")
        long totalElements,

        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }
}
