package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing salary data associated with specific salary grades.
 * Provides endpoints for retrieving, creating, updating, and deleting salary data entries.
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/v1/salary-data")
@Tag(
        name = "Salary Data",
        description = "Endpoints for managing salary data associated with specific salary grades."
)
@Hidden
public class SalaryDataController {

    private final SalaryDataService salaryDataService;

    /**
     * Retrieves a paginated list of all salary data entries, optionally filtered by salary grade ID.
     *
     * @param page The page number (1-based).
     * @param size The number of records per page.
     * @return A ResponseEntity containing a {@link StandardApiResponse} with a list of SalaryDataResponse and pagination metadata.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all salary data",
            description = """
                    Retrieve a paginated list of all salary data entries. \
                    Supports pagination through 'page' and 'size' query parameters.
                    1-based page indexing is used (i.e., the first page is page 1).
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of salary data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<SalaryDataResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) throws BadRequestException {
        log.debug("Fetching all salary data with page: {} and size: {}", page, size);
        final var PAGE = salaryDataService.findAll(PageRequest.of(page - 1, size));
        return ResponseEntity.ok(StandardApiResponse.success(
                PAGE.getContent(),
                PaginationMeta.builder()
                        .page(PAGE.getNumber() + 1)
                        .size(PAGE.getSize())
                        .totalElements(PAGE.getTotalElements())
                        .totalPages(PAGE.getTotalPages())
                        .build()
        ));
    }

    /**
     * Retrieves a specific salary data entry by its ID, optionally validating against a salary grade ID.
     *
     * @param id The ID of the salary data to retrieve.
     * @return A ResponseEntity containing a {@link StandardApiResponse} with the SalaryDataResponse.
     * @throws NotFoundException If the salary data or salary grade is not found.
     */
    @Operation(
            summary = "Get salary data by ID",
            description = """
                    Retrieve a specific salary data entry by its ID.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> get(
            @Parameter(description = "ID of the salary data to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Fetching salary data with id: {}", id);
        var response = salaryDataService.findById(id);

        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    /**
     * Creates a new salary data entry for a specific salary grade.
     *
     * @param salaryDataRequest the details of the salary data to create
     * @return a ResponseEntity containing a StandardApiResponse with the created SalaryDataResponse
     */
    @Operation(
            summary = "Create salary data",
            description = """
                    Create a new salary data entry for a specific salary grade.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Salary data with the same step and amount already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Salary data details to create", required = true)
            SalaryDataRequest salaryDataRequest
    ) {
        log.debug("Request to create salaryDataRequest: {}", salaryDataRequest);
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.create(salaryDataRequest)));
    }

    /**
     * Updates an existing salary data entry by its ID.
     *
     * @param id                The ID of the salary data to update.
     * @param salaryDataRequest The updated salary data details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated SalaryDataResponse.
     * @throws BadRequestException If invalid data or parameters are provided.
     * @throws NotFoundException   If the salary data or salary grade is not found.
     */
    @Operation(
            summary = "Update salary data",
            description = """
                    Update an existing salary data entry by its ID. If a salary grade ID is provided in the request body, \
                    it will validate that the salary data belongs to that salary grade.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the salary data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data to update not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> update(
            @Parameter(description = "ID of the salary data to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated salary data details", required = true)
            @RequestBody @Valid SalaryDataRequest.WithoutSalaryGradeId salaryDataRequest
    ) throws BadRequestException {
        log.info("Request to update salary data with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.update(id, salaryDataRequest)));
    }

    /**
     * Deletes a specific salary data entry by its ID, optionally validating against a salary grade ID.
     *
     * @param id            The ID of the salary data to delete.
     * @param salaryGradeId Optional ID of the salary grade to validate association.
     * @return A ResponseEntity indicating success or failure of the deletion.
     */
    @Operation(
            summary = "Delete salary data",
            description = """
                    Delete a specific salary data entry by its ID. Optionally validate it belongs to a specific salary grade using the 'salaryGradeId' parameter.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the salary data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardApiResponse<Void>> delete(
            @Parameter(description = "ID of the salary data to delete", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Optional ID of the salary grade to validate association")
            @RequestParam(required = false) String salaryGradeId
    ) {
        log.debug("Request to delete salaryData with id [{}] and salaryGradeId [{}]", id, salaryGradeId);
        var isDeleted = salaryDataService.delete(id, salaryGradeId);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("SalaryData with id [%s] and SalaryGrade with [%s] not found".formatted(id, salaryGradeId))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}