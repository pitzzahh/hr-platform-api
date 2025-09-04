package dev.araopj.hrplatformapi.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * A generic API response wrapper that includes a timestamp, data, error details, and pagination metadata.
 *
 * @param <T> the type of the data included in the response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardApiResponse<T> {

    private Instant timestamp = Instant.now();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiError error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PaginationMeta pagination;

    /**
     * Static factory method to create a successful StandardApiResponse with data.
     *
     * @param data the data to include in the response
     * @param <T>  the type of the data
     * @return a successful StandardApiResponse containing the data
     */
    public static <T> StandardApiResponse<T> success(T data) {
        return StandardApiResponse.<T>builder()
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    /**
     * Static factory method to create a successful StandardApiResponse with data and pagination metadata.
     *
     * @param data       the data to include in the response
     * @param pagination the pagination metadata
     * @param <T>        the type of the data
     * @return a successful StandardApiResponse containing the data and pagination info
     */
    public static <T> StandardApiResponse<T> success(T data, PaginationMeta pagination) {
        return StandardApiResponse.<T>builder()
                .timestamp(Instant.now())
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * Static factory method to create a failure StandardApiResponse with an error.
     *
     * @param error the error details
     * @param <T>   the type of the data
     * @return a failure StandardApiResponse containing the error
     */
    public static <T> StandardApiResponse<T> failure(ApiError error) {
        return StandardApiResponse.<T>builder()
                .timestamp(Instant.now())
                .error(error)
                .build();
    }
}