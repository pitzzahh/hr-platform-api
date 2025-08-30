package dev.araopj.hrplatformapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.Instant;

/**
 * A generic API response wrapper that includes a timestamp, data, error details, and pagination metadata.
 *
 * @param <T> the type of the data included in the response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApiResponse<T> {

    private Instant timestamp = Instant.now();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiError error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PaginationMeta pagination;

    /**
     * Static factory method to create a successful ApiResponse with data.
     *
     * @param data the data to include in the response
     * @param <T>  the type of the data
     * @return a successful ApiResponse containing the data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .data(data)
                .build();
    }

    /**
     * Static factory method to create a successful ApiResponse with data and pagination metadata.
     *
     * @param data       the data to include in the response
     * @param pagination the pagination metadata
     * @param <T>        the type of the data
     * @return a successful ApiResponse containing the data and pagination info
     */
    public static <T> ApiResponse<T> success(T data, PaginationMeta pagination) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .data(data)
                .pagination(pagination)
                .build();
    }

    /**
     * Static factory method to create a failure ApiResponse with an error.
     *
     * @param error the error details
     * @param <T>   the type of the data
     * @return a failure ApiResponse containing the error
     */
    public static <T> ApiResponse<T> failure(ApiError error) {
        return ApiResponse.<T>builder()
                .timestamp(Instant.now())
                .error(error)
                .build();
    }
}