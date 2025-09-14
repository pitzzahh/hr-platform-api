package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationSalaryOverrideRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationSalaryOverrideResponse;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationSalaryOverrideService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing employment information salary override data.
 * Provides endpoints for retrieving, creating, updating, and deleting salary override entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/employment-information-salary-overrides")
@RequiredArgsConstructor
@Tag(
        name = "Employment Information Salary Overrides",
        description = "Endpoints for managing employment information salary override data."
)
public class EmploymentInformationSalaryOverrideController {

    private final EmploymentInformationSalaryOverrideService employmentInformationSalaryOverrideService;

    /**
     * Retrieves a list of all employment information salary override entries.
     *
     * @return A ResponseEntity containing a StandardApiResponse with a list of EmploymentInformationSalaryOverrideResponse.
     */
    @Operation(
            summary = "Get all salary overrides",
            description = "Retrieve a list of all employment information salary override entries.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of salary overrides",
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
    public ResponseEntity<StandardApiResponse<List<EmploymentInformationSalaryOverrideResponse>>> all() {
        log.debug("Fetching all employment information salary overrides");
        return ResponseEntity.ok(StandardApiResponse.success(employmentInformationSalaryOverrideService.findAll()));
    }

    /**
     * Retrieves a specific employment information salary override by its ID.
     *
     * @param id The ID of the salary override to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the EmploymentInformationSalaryOverrideResponse.
     * @throws NotFoundException If the salary override is not found.
     */
    @Operation(
            summary = "Get salary override by ID",
            description = "Retrieve a specific employment information salary override by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary override",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary override not found",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationSalaryOverrideResponse>> get(
            @Parameter(description = "ID of the salary override to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Fetching salary override with id: {}", id);
        var response = employmentInformationSalaryOverrideService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new employment information salary override.
     *
     * @param employmentInformationSalaryOverrideRequest The details of the salary override to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created EmploymentInformationSalaryOverrideResponse.
     * @throws IllegalArgumentException If the salary override already exists for the given employment information.
     */
    @Operation(
            summary = "Create salary override",
            description = "Create a new employment information salary override entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary override",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary override data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Salary override already exists for the employment information",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationSalaryOverrideResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Salary override details to create", required = true)
            EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest
    ) {
        log.debug("Request to create salary override: {}", employmentInformationSalaryOverrideRequest);
        return ResponseEntity.ok(StandardApiResponse.success(
                employmentInformationSalaryOverrideService.create(employmentInformationSalaryOverrideRequest)
        ));
    }

    /**
     * Updates an existing employment information salary override by its ID.
     *
     * @param id                                         The ID of the salary override to update.
     * @param employmentInformationSalaryOverrideRequest The updated salary override details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated EmploymentInformationSalaryOverrideResponse.
     * @throws BadRequestException If invalid data or ID is provided.
     * @throws NotFoundException   If the salary override is not found.
     */
    @Operation(
            summary = "Update salary override",
            description = "Update an existing employment information salary override by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the salary override",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary override data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary override not found",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationSalaryOverrideResponse>> update(
            @Parameter(description = "ID of the salary override to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated salary override details", required = true)
            @RequestBody @Valid EmploymentInformationSalaryOverrideRequest employmentInformationSalaryOverrideRequest
    ) throws BadRequestException {
        log.info("Request to update salary override with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(
                employmentInformationSalaryOverrideService.update(id, employmentInformationSalaryOverrideRequest)
        ));
    }

    /**
     * Deletes a specific employment information salary override by its ID.
     *
     * @param id The ID of the salary override to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the salary override is not found.
     */
    @Operation(
            summary = "Delete salary override",
            description = "Delete a specific employment information salary override by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the salary override",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary override not found",
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
            @Parameter(description = "ID of the salary override to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Request to delete salary override with id [{}]", id);
        var isDeleted = employmentInformationSalaryOverrideService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Employment information salary override with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}