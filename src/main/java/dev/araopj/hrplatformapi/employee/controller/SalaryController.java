package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.SalaryRequest;
import dev.araopj.hrplatformapi.employee.dto.response.SalaryResponse;
import dev.araopj.hrplatformapi.employee.service.SalaryService;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing salary data.
 * Provides endpoints for retrieving, creating, updating, and deleting salary entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/salaries")
@RequiredArgsConstructor
@Tag(
        name = "Salaries",
        description = "Endpoints for managing salary data."
)
public class SalaryController {

    private final SalaryService salaryService;

    /**
     * Retrieves a paginated list of all salary entries.
     *
     * @param page The page number (1-based).
     * @param size The number of records per page.
     * @return A ResponseEntity containing a StandardApiResponse with a list of SalaryResponse and pagination metadata.
     * @throws InvalidRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all salaries",
            description = "Retrieve a paginated list of all salary entries. Supports pagination through 'page' and 'size' query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of salaries",
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
    public ResponseEntity<StandardApiResponse<List<SalaryResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) throws InvalidRequestException {
        log.debug("Fetching all salaries with page: {} and size: {}", page, size);
        final var PAGE = salaryService.findAll(PageRequest.of(page - 1, size));
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
     * Retrieves a specific salary by its ID.
     *
     * @param id The ID of the salary to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the SalaryResponse.
     * @throws NotFoundException If the salary is not found.
     */
    @Operation(
            summary = "Get salary by ID",
            description = "Retrieve a specific salary by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary not found",
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
    public ResponseEntity<StandardApiResponse<SalaryResponse>> get(
            @Parameter(description = "ID of the salary to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Fetching salary with id: {}", id);
        var response = salaryService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new salary.
     *
     * @param salaryRequest The details of the salary to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created SalaryResponse.
     * @throws InvalidRequestException If the salary data is invalid.
     */
    @Operation(
            summary = "Create salary",
            description = "Create a new salary entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary",
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
    public ResponseEntity<StandardApiResponse<SalaryResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Salary details to create", required = true)
            SalaryRequest salaryRequest
    ) {
        log.debug("Request to create salary: {}", salaryRequest);
        return ResponseEntity.ok(StandardApiResponse.success(salaryService.create(salaryRequest)));
    }

    /**
     * Updates an existing salary by its ID.
     *
     * @param id            The ID of the salary to update.
     * @param salaryRequest The updated salary details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated SalaryResponse.
     * @throws InvalidRequestException If invalid data or ID is provided.
     * @throws NotFoundException       If the salary is not found.
     */
    @Operation(
            summary = "Update salary",
            description = "Update an existing salary by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the salary",
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
                            description = "Salary not found",
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
    public ResponseEntity<StandardApiResponse<SalaryResponse>> update(
            @Parameter(description = "ID of the salary to update", required = true)
            @PathVariable
            @Valid
            @NotNull(message = "Salary id cannot be null")
            @NotBlank(message = "Salary id cannot be blank")
            String id,
            @Parameter(description = "Updated salary details", required = true)
            @RequestBody
            @Valid
            SalaryRequest salaryRequest
    ) throws InvalidRequestException {
        log.info("Request to update salary with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(salaryService.update(id, salaryRequest)));
    }

    /**
     * Deletes a specific salary by its ID.
     *
     * @param id The ID of the salary to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the salary is not found.
     */
    @Operation(
            summary = "Delete salary",
            description = "Delete a specific salary by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the salary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary not found",
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
            @Parameter(description = "ID of the salary to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Request to delete salary with id [{}]", id);
        var isDeleted = salaryService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Salary with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}