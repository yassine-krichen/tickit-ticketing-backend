package com.sofrecom.tickit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic wrapper for paginated API responses.
 *
 * @param <T> The type of the content in the page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse<T> {

    /** The content of the current page. */
    private List<T> content;

    /** The current page number (zero-based). */
    private int pageNumber;

    /** The number of items requested per page. */
    private int pageSize;

    /** The total number of items available across all pages. */
    private long totalItems;

    /** The total number of pages available. */
    private int totalPages;

    /** Indicates if this is the last page. */
    private boolean isLastPage;

    /**
     * Creates a PaginatedResponse from a Spring Data Page object.
     *
     * @param page The Page object.
     * @param content The mapped content for the response.
     * @param <U> The type of the original Page content.
     * @param <T> The type of the mapped response content.
     * @return A PaginatedResponse instance.
     */
    public static <U, T> PaginatedResponse<T> fromPage(Page<U> page, List<T> content) {
        return PaginatedResponse.<T>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLastPage(page.isLast())
                .build();
    }
}