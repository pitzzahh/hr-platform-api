package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.service.EmployeeService;
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
 * REST controller for managing employee data.
 * Provides endpoints for retrieving, creating, updating, and deleting employee entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/employees")
@RequiredArgsConstructor
@Tag(
        name = "Employees",
        description = "Endpoints for managing employee data."
)
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Retrieves a paginated list of all employee entries.
     *
     * @param page The page number (1-based).
     * @param size The number of records per page.
     * @return A ResponseEntity containing a StandardApiResponse with a list of EmployeeResponse and pagination metadata.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all employees",
            description = "Retrieve a paginated list of all employee entries. Supports pagination through 'page' and 'size' query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of employees",
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
    public ResponseEntity<StandardApiResponse<List<EmployeeResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Include ID Documents in the response", example = "false")
            @RequestParam(defaultValue = "false") boolean includeIdDocuments,
            @Parameter(description = "Include Employment Information in the response", example = "false")
            @RequestParam(defaultValue = "false") boolean includeEmploymentInformation
    ) throws BadRequestException {
        log.debug("Fetching all employees with page: {} and size: {}", page, size);
        final var PAGE = employeeService.findAll(
                PageRequest.of(page - 1, size),
                includeIdDocuments,
                includeEmploymentInformation
        );
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
     * Retrieves a specific employee by its ID or user ID.
     *
     * @param id     The ID of the employee to retrieve.
     * @param userId (Optional) The user ID associated with the employee.
     * @return A ResponseEntity containing a StandardApiResponse with the EmployeeResponse.
     * @throws NotFoundException If the employee is not found.
     */
    @Operation(
            summary = "Get employee by ID or its user ID",
            description = """
                    Retrieve a specific employee by its ID or user ID.
                    If both 'id' and 'userId' are provided, 'id' takes precedence.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
    public ResponseEntity<StandardApiResponse<EmployeeResponse>> get(
            @Parameter(description = "ID of the employee to retrieve", required = true)
            @PathVariable String id,
            @Parameter(description = "User ID of the employee to retrieve")
            @RequestParam(required = false) String userId
    ) {
        log.debug("Fetching employee with id [{}] or user id [{}]", id, userId);
        var response = (id != null && !id.isEmpty() && userId != null && !userId.isEmpty())
                ? employeeService.findById(id)
                : (id == null && userId != null && !userId.isEmpty() ? employeeService.findByUserId(userId) : employeeService.findById(id));
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new employee.
     *
     * @param employeeRequest The details of the employee to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created EmployeeResponse.
     * @throws IllegalArgumentException If the employee already exists with the given identifiers.
     */
    @Operation(
            summary = "Create employee",
            description = "Create a new employee entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employee data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Employee with the same identifiers already exists",
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
    public ResponseEntity<StandardApiResponse<EmployeeResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Employee details to create", required = true)
            EmployeeRequest employeeRequest
    ) {
        log.debug("Request to create employee: {}", employeeRequest);
        return ResponseEntity.ok(StandardApiResponse.success(employeeService.create(employeeRequest)));
    }

    /**
     * Updates an existing employee by its ID.
     *
     * @param id              The ID of the employee to update.
     * @param employeeRequest The updated employee details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated EmployeeResponse.
     * @throws BadRequestException If invalid data or ID is provided.
     * @throws NotFoundException   If the employee is not found.
     */
    @Operation(
            summary = "Update employee",
            description = "Update an existing employee by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid employee data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
    public ResponseEntity<StandardApiResponse<EmployeeResponse>> update(
            @Parameter(description = "ID of the employee to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated employee details", required = true)
            @RequestBody @Valid EmployeeRequest employeeRequest
    ) throws BadRequestException {
        log.info("Request to update employee with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(employeeService.update(id, employeeRequest)));
    }

    /**
     * Deletes a specific employee by its ID.
     *
     * @param id The ID of the employee to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the employee is not found.
     */
    @Operation(
            summary = "Delete employee",
            description = "Delete a specific employee by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the employee",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Employee not found",
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
            @Parameter(description = "ID of the employee to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Request to delete employee with id [{}]", id);
        var isDeleted = employeeService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Employee with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}