package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationService;
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
 * REST controller for managing employment information data for a specific employee.
 * Provides endpoints for retrieving, creating, updating, and deleting employment information entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/employees/{employeeId}/employment-information")
@RequiredArgsConstructor
@Tag(
        name = "Employment Information",
        description = "Endpoints for managing employment information data for a specific employee."
)
public class EmploymentInformationController {

    private final EmploymentInformationService employmentInformationService;

    /**
     * Retrieves a paginated list of employment information entries for a specific employee.
     *
     * @param employeeId The ID of the employee to filter employment information.
     * @param page       The page number (1-based).
     * @param size       The number of records per page.
     * @return A ResponseEntity containing a StandardApiResponse with a list of EmploymentInformationResponse and pagination metadata.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get employment information for an employee",
            description = "Retrieve a paginated list of employment information entries for a specific employee. Supports pagination through 'page' and 'size' query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of employment information",
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
    public ResponseEntity<StandardApiResponse<List<EmploymentInformationResponse>>> all(
            @Parameter(description = "ID of the employee to filter employment information", required = true)
            @PathVariable String employeeId,
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) throws BadRequestException {
        log.debug("Fetching employment information for employeeId: {} with page: {} and size: {}", employeeId, page, size);
        final var PAGE = employmentInformationService.findByEmployeeId(employeeId, PageRequest.of(page - 1, size));
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
     * Retrieves a specific employment information entry by its ID for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param id         The ID of the employment information to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the EmploymentInformationResponse.
     * @throws NotFoundException   If the employment information is not found.
     * @throws BadRequestException If the employment information does not belong to the specified employee.
     */
    @Operation(
            summary = "Get employment information by ID",
            description = "Retrieve a specific employment information entry by its ID for a specific employee.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the employment information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employment information or employee ID provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employment information not found",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationResponse>> get(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable String employeeId,
            @Parameter(description = "ID of the employment information to retrieve", required = true)
            @PathVariable String id
    ) throws BadRequestException {
        log.debug("Fetching employment information with id: {} for employeeId: {}", id, employeeId);
        var response = employmentInformationService.findById(id);
        if (response.isPresent() && !response.get().employeeResponse().id().equals(employeeId)) {
            throw new BadRequestException("Employment information with id [%s] does not belong to employee with id [%s]".formatted(id, employeeId));
        }
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new employment information entry for a specific employee.
     *
     * @param employeeId                   The ID of the employee.
     * @param employmentInformationRequest The details of the employment information to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created EmploymentInformationResponse.
     * @throws IllegalArgumentException If the employment information already exists for the given employee.
     * @throws BadRequestException      If the employeeId in the request does not match the path variable.
     */
    @Operation(
            summary = "Create employment information",
            description = "Create a new employment information entry for a specific employee.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the employment information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employment information data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Employment information already exists for the employee",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationResponse>> create(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable String employeeId,
            @Valid
            @RequestBody
            @Parameter(description = "Employment information details to create", required = true)
            EmploymentInformationRequest employmentInformationRequest
    ) throws BadRequestException {
        log.debug("Request to create employment information for employeeId: {}", employeeId);
        if (!employeeId.equals(employmentInformationRequest.employeeId())) {
            throw new BadRequestException("Employee ID in request body [%s] does not match path variable [%s]".formatted(employmentInformationRequest.employeeId(), employeeId));
        }
        return ResponseEntity.ok(StandardApiResponse.success(
                employmentInformationService.create(employeeId, employmentInformationRequest)
        ));
    }

    /**
     * Updates an existing employment information entry by its ID for a specific employee.
     *
     * @param employeeId                   The ID of the employee.
     * @param id                           The ID of the employment information to update.
     * @param employmentInformationRequest The updated employment information details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated EmploymentInformationResponse.
     * @throws BadRequestException If invalid data or ID is provided or employeeId does not match.
     * @throws NotFoundException   If the employment information is not found.
     */
    @Operation(
            summary = "Update employment information",
            description = "Update an existing employment information entry by its ID for a specific employee.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the employment information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employment information data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employment information not found",
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
    public ResponseEntity<StandardApiResponse<EmploymentInformationResponse>> update(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable @NotNull String employeeId,
            @Parameter(description = "ID of the employment information to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated employment information details", required = true)
            @RequestBody @Valid EmploymentInformationRequest employmentInformationRequest
    ) throws BadRequestException {
        log.info("Request to update employment information with id [{}] for employeeId: {}", id, employeeId);
        if (!employeeId.equals(employmentInformationRequest.employeeId())) {
            throw new BadRequestException("Employee ID in request body [%s] does not match path variable [%s]".formatted(employmentInformationRequest.employeeId(), employeeId));
        }
        var response = employmentInformationService.findById(id);
        if (response.isPresent() && !response.get().employeeResponse().id().equals(employeeId)) {
            throw new BadRequestException("Employment information with id [%s] does not belong to employee with id [%s]".formatted(id, employeeId));
        }
        return ResponseEntity.ok(StandardApiResponse.success(
                employmentInformationService.update(id, employmentInformationRequest)
        ));
    }

    /**
     * Deletes a specific employment information entry by its ID for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param id         The ID of the employment information to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException   If the employment information is not found.
     * @throws BadRequestException If the employment information does not belong to the specified employee.
     */
    @Operation(
            summary = "Delete employment information",
            description = "Delete a specific employment information entry by its ID for a specific employee.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the employment information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employment information or employee ID provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employment information not found",
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
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable @NotNull String employeeId,
            @Parameter(description = "ID of the employment information to delete", required = true)
            @PathVariable @NotNull String id
    ) throws BadRequestException {
        log.debug("Request to delete employment information with id [{}] for employeeId: {}", id, employeeId);
        var response = employmentInformationService.findById(id);
        if (response.isPresent() && !response.get().employeeResponse().id().equals(employeeId)) {
            throw new BadRequestException("Employment information with id [%s] does not belong to employee with id [%s]".formatted(id, employeeId));
        }
        var isDeleted = employmentInformationService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Employment information with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}