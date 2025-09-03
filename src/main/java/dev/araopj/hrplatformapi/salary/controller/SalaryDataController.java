package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.service.ISalaryDataService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import dev.araopj.hrplatformapi.utils.enums.FetchType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.araopj.hrplatformapi.utils.enums.FetchType.BY_PATH_VARIABLE;

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
public class SalaryDataController {

    private final ISalaryDataService salaryDataService;

    /**
     * Retrieves a list of salary data entries, optionally filtered by salary grade ID.
     *
     * @param salaryGradeId   Optional ID of the salary grade to filter salary data.
     * @param withSalaryGrade If true, includes salary grade details in the response.
     * @param limit           Maximum number of results to return (default: 10).
     * @return A ResponseEntity containing a StandardApiResponse with a list of SalaryDataResponse objects.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all salary data",
            description = """
                    Retrieve a list of all salary data entries, optionally filtered by salary grade ID. \
                    Use the 'limit' parameter to control the number of results. \
                    Optionally include salary grade details with the 'withSalaryGrade' parameter.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of salary data",
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
            @Parameter(description = "Optional ID of the salary grade to filter salary data")
            @RequestParam(required = false) String salaryGradeId,
            @Parameter(description = "Include salary grade details in the response")
            @RequestParam(required = false) boolean withSalaryGrade,
            @Parameter(description = "Maximum number of results to return", example = "10")
            @RequestParam(defaultValue = "10", required = false) @Valid int limit
    ) throws BadRequestException {
        log.debug("Fetching all salary data with salaryGradeId: {}, withSalaryGrade: {}, limit: {}",
                salaryGradeId, withSalaryGrade, limit);
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.findAll(
                salaryGradeId,
                withSalaryGrade,
                limit
        )));
    }

    /**
     * Retrieves a specific salary data entry by its ID, optionally validating against a salary grade ID.
     *
     * @param id            The ID of the salary data to retrieve.
     * @param salaryGradeId Optional ID of the salary grade to validate association.
     * @param fetchType     The fetching strategy (default: BY_PATH_VARIABLE).
     * @return A ResponseEntity containing a StandardApiResponse with the SalaryDataResponse.
     * @throws NotFoundException If the salary data or salary grade is not found.
     */
    @Operation(
            summary = "Get salary data by ID",
            description = """
                    Retrieve a specific salary data entry by its ID. Optionally validate it belongs to a specific salary grade using the 'salaryGradeId' parameter. \
                    Choose the fetching strategy with the 'fetchType' parameter.
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
            @PathVariable String id,
            @Parameter(description = "Optional ID of the salary grade to validate association")
            @RequestParam(required = false) String salaryGradeId,
            @Parameter(description = "Fetching strategy for the salary data", example = "BY_PATH_VARIABLE")
            @RequestParam(defaultValue = "BY_PATH_VARIABLE", required = false) @Valid FetchType fetchType
    ) {
        log.debug("Fetching salary data with id: {}, salaryGradeId: {}, fetchType: {}", id, salaryGradeId, fetchType);
        var response = fetchType == BY_PATH_VARIABLE
                ? salaryDataService.findById(id)
                : salaryDataService.findByIdAndSalaryGradeId(id, salaryGradeId);

        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    /**
     * Creates a new salary data entry for a specific salary grade.
     *
     * @param salaryDataRequest The salary data details to create.
     * @param salaryGradeId     Optional ID of the salary grade from query parameter.
     * @param checkType         The validation strategy for the salary grade ID.
     * @return A ResponseEntity containing a StandardApiResponse with the created SalaryDataResponse.
     * @throws BadRequestException If invalid data or parameters are provided.
     * @throws NotFoundException   If the salary grade is not found.
     */
    @Operation(
            summary = "Create salary data",
            description = """
                    Create a new salary data entry for a specific salary grade. \
                    Validate the salary grade ID using the 'checkType' parameter to specify whether it comes from the request body or query parameter.
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
            @Parameter(description = "Salary data details to create", required = true)
            @RequestBody @NotNull @Valid SalaryDataRequest salaryDataRequest,
            @Parameter(description = "Optional ID of the salary grade from query parameter")
            @RequestParam(required = false) @Nullable String salaryGradeId,
            @Parameter(description = "Validation strategy for salary grade ID", example = "CHECK_PARENT_FROM_REQUEST_BODY")
            @RequestParam(defaultValue = "CHECK_PARENT_FROM_REQUEST_BODY") @NotNull CheckType checkType
    ) throws BadRequestException {
        log.debug("Request to create salaryDataRequest: {}", salaryDataRequest);

        return ResponseEntity.ok(
                StandardApiResponse.success(salaryDataService.create(salaryDataRequest, salaryGradeId, checkType)
                ));
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
            @RequestBody @Valid SalaryDataRequest salaryDataRequest
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