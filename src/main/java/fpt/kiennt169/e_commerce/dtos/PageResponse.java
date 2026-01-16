package fpt.kiennt169.e_commerce.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.data.domain.Page;

@Schema(description = "Paginated response wrapper")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    @Schema(description = "List of items in current page")
    private List<T> content;

    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int pageNumber;

    @Schema(description = "Page size", example = "10")
    private int pageSize;

    @Schema(description = "Total number of elements", example = "100")
    private long totalElements;

    @Schema(description = "Total number of pages", example = "10")
    private int totalPages;

    @Schema(description = "Is this the first page?", example = "true")
    private boolean first;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;

    @Schema(description = "Is the content empty?", example = "false")
    private boolean empty;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
