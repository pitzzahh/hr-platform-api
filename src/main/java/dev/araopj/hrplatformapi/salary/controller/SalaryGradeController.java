package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.service.impl.SalaryGradeServiceImp;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing salary grades.
 * Provides endpoints for retrieving, creating, updating, and deleting salary grade entries.
 */
@Tag(
        name = "Salary Grade",
        description = """
                Endpoints for managing salary grades, including operations to retrieve, create, update, and delete salary grade entries.
                """
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/salary-grades")
@Hidden
public class SalaryGradeController {

    private final SalaryGradeServiceImp salaryGradeService;

    /**
     * Retrieves a list of all salary grade entries with pagination support.
     *
     * @param page              The page number for pagination (1-based).
     * @param size              The number of records per page for pagination.
     * @param includeSalaryData If true, includes associated salary data in the response.
     * @return A ResponseEntity containing a StandardApiResponse with a list of SalaryGradeResponse objects.
     */
    @Operation(
            summary = "Get all salary grades",
            description = """
                     Retrieve a list of all salary grade entries.\s
                     Optionally include associated salary data using the 'includeSalaryData' parameter.
                    \s""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of salary grades",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
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
    public ResponseEntity<StandardApiResponse<List<SalaryGradeResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Valid
            @RequestParam(defaultValue = "false", required = false)
            @Parameter(description = "Include associated salary data in the response", example = "false")
            boolean includeSalaryData,
            @Parameter(description = "Sorting criteria in the format: property,(asc|desc). Default is salaryGrade,asc", example = "salaryGrade,asc")
            @RequestParam(defaultValue = "salaryGrade,asc") String sort
    ) {
        log.debug("Fetching all salary grades with includeSalaryData: {}, sort: {}", includeSalaryData, sort);
        var sortParams = sort.split(",");
        var direction = Sort.Direction.fromString(sortParams[1]);
        var sortBy = sortParams[0];
        var pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        final var salaryGradesPage = salaryGradeService.findAll(pageable, includeSalaryData);

        return ResponseEntity.ok(
                StandardApiResponse.success(
                        salaryGradesPage.getContent(),
                        PaginationMeta.builder()
                                .page(salaryGradesPage.getNumber() + 1)
                                .size(salaryGradesPage.getSize())
                                .totalElements(salaryGradesPage.getTotalElements())
                                .totalPages(salaryGradesPage.getTotalPages())
                                .build()
                )
        );
    }

    /**
     * Retrieves a specific salary grade entry by its ID.
     *
     * @param id                The ID of the salary grade to retrieve.
     * @param includeSalaryData If true, includes associated salary data in the response.
     * @return A ResponseEntity containing a StandardApiResponse with the SalaryGradeResponse.
     * @throws NotFoundException        If the salary grade is not found.
     * @throws IllegalArgumentException If the provided ID is invalid (e.g., null or empty).
     */
    @Operation(
            summary = "Get salary grade by ID",
            description = """
                     Retrieve a specific salary grade entry by its ID.\s
                     Optionally include associated salary data using the 'includeSalaryData' parameter.
                    \s""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary grade not found",
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
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> get(
            @NotBlank
            @PathVariable
            @Parameter(description = "ID of the salary grade to retrieve", required = true)
            String id,
            @Valid
            @RequestParam(defaultValue = "false", required = false)
            @Parameter(description = "Include associated salary data in the response", example = "false")
            boolean includeSalaryData
    ) {
        log.debug("Fetching salary grade with id: {}, includeSalaryData: {}", id, includeSalaryData);
        var response = salaryGradeService.findById(id, includeSalaryData);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.SALARY_GRADE));
    }

    /**
     * Creates a new salary grade entry.
     *
     * @param salaryGradeRequests The salary grade details to create.
     * @param includeSalaryData   If true, includes associated salary data in the creation process.
     * @return A ResponseEntity containing a StandardApiResponse with the created SalaryGradeResponse.
     * @throws BadRequestException If the salary grade already exists with the same salary grade and effective date or if invalid data is provided.
     */
    @Operation(
            summary = "Create salary grade",
            description = """
                     Creates salary grades from a single SalaryGradeRequest or an array of SalaryGradeRequests\s
                     Optionally include associated salary data using the 'includeSalaryData' parameter.
                    \s""",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary grade data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Salary grade with the same salary grade and effective date already exists",
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
    public ResponseEntity<StandardApiResponse<List<SalaryGradeResponse>>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Salary grade details to create (single object or array)", required = true)
            List<SalaryGradeRequest> salaryGradeRequests,
            @Valid
            @Parameter(description = "Include associated salary data in the creation process", example = "false")
            @RequestParam(defaultValue = "false", required = false)
            boolean includeSalaryData
    ) throws BadRequestException {
        log.debug("Request to create salaryGradeRequests: {}", salaryGradeRequests);
        final var batch = salaryGradeService.create(salaryGradeRequests, includeSalaryData);
        if (batch.size() == 1) {
            URI location = URI.create(String.format("/api/v1/salary-grades/%s", batch.getFirst().id()));
            return ResponseEntity.created(location).body(StandardApiResponse.success(batch));
        }
        return ResponseEntity.ok(StandardApiResponse.success(batch));
    }

    /**
     * Updates an existing salary grade entry by its ID.
     *
     * @param id                 The ID of the salary grade to update.
     * @param salaryGradeRequest The updated salary grade details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated SalaryGradeResponse.
     * @throws BadRequestException If the provided ID is invalid.
     * @throws NotFoundException   If the salary grade is not found.
     */
    @Operation(
            summary = "Update salary grade by ID",
            description = """
                    Update an existing salary grade entry by its ID with the provided details.
                    Note that this endpoint does not update associated salary data.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID or salary grade data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary grade not found",
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
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> update(
            @PathVariable
            @NotNull(message = "id of salary grade must not be null")
            @NotBlank(message = "id of salary grade must not be blank")
            @Parameter(description = "ID of the salary grade to update", required = true)
            String id,
            @Valid
            @RequestBody
            @Parameter(description = "Updated salary grade details", required = true)
            SalaryGradeRequest salaryGradeRequest
    ) throws BadRequestException {
        log.debug("Request to update salary grade with id: {}, salaryGradeRequest: {}", id, salaryGradeRequest);
        SalaryGradeResponse updatedSalaryGrade = salaryGradeService.update(id, salaryGradeRequest);
        return ResponseEntity.ok(StandardApiResponse.success(updatedSalaryGrade));
    }

    /**
     * Deletes a salary grade entry by its ID. All associated salary data will also be deleted.
     *
     * @param id The ID of the salary grade to delete.
     * @return A ResponseEntity containing a StandardApiResponse with a boolean indicating deletion success.
     * @throws NotFoundException   If the salary grade is not found.
     * @throws BadRequestException If the provided ID is invalid.
     */
    @Operation(
            summary = "Delete salary grade by ID",
            description = "Delete a salary grade entry by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary grade not found",
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
    public ResponseEntity<StandardApiResponse<Boolean>> delete(
            @Parameter(description = "ID of the salary grade to delete", required = true)
            @PathVariable
            @NotBlank(message = "id of salary grade must not be blank")
            @NotNull(message = "id of salary grade must not be null")
            String id
    ) throws BadRequestException {
        log.debug("Request to delete salary grade with id: {}", id);
        boolean deleted = salaryGradeService.delete(id);
        return ResponseEntity.ok(StandardApiResponse.success(deleted));
    }
}