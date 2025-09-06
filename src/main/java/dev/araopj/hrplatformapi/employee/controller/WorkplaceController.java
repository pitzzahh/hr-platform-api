package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.service.WorkplaceService;
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
 * REST controller for managing workplace data.
 * Provides endpoints for retrieving, creating, updating, and deleting workplace entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/workplaces")
@RequiredArgsConstructor
@Tag(
        name = "Workplaces",
        description = "Endpoints for managing workplace data."
)
public class WorkplaceController {

    private final WorkplaceService workplaceService;

    /**
     * Retrieves a paginated list of all workplace entries.
     *
     * @param page The page number (1-based).
     * @param size The number of records per page.
     * @return A ResponseEntity containing a StandardApiResponse with a list of WorkplaceResponse and pagination metadata.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all workplaces",
            description = "Retrieve a paginated list of all workplace entries. Supports pagination through 'page' and 'size' query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of workplaces",
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
    public ResponseEntity<StandardApiResponse<List<WorkplaceResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) throws BadRequestException {
        log.debug("Fetching all workplaces with page: {} and size: {}", page, size);
        final var PAGE = workplaceService.findAll(PageRequest.of(page - 1, size));
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
     * Retrieves a specific workplace by its ID.
     *
     * @param id The ID of the workplace to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the WorkplaceResponse.
     * @throws NotFoundException If the workplace is not found.
     */
    @Operation(
            summary = "Get workplace by ID",
            description = "Retrieve a specific workplace by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the workplace",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Workplace not found",
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
    public ResponseEntity<StandardApiResponse<WorkplaceResponse>> get(
            @Parameter(description = "ID of the workplace to retrieve", required = true)
            @PathVariable String id
    ) throws NotFoundException {
        log.debug("Fetching workplace with id: {}", id);
        var response = workplaceService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new workplace.
     *
     * @param workplaceRequest The details of the workplace to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created WorkplaceResponse.
     * @throws IllegalArgumentException If the workplace code and name already exist for the employment information.
     */
    @Operation(
            summary = "Create workplace",
            description = "Create a new workplace entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the workplace",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid workplace data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Workplace with the same code and name already exists for the employment information",
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
    public ResponseEntity<StandardApiResponse<WorkplaceResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Workplace details to create", required = true)
            WorkplaceRequest workplaceRequest
    ) {
        log.debug("Request to create workplace: {}", workplaceRequest);
        return ResponseEntity.ok(StandardApiResponse.success(workplaceService.create(workplaceRequest)));
    }

    /**
     * Updates an existing workplace by its ID.
     *
     * @param id               The ID of the workplace to update.
     * @param workplaceRequest The updated workplace details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated WorkplaceResponse.
     * @throws BadRequestException If invalid data or ID is provided.
     * @throws NotFoundException   If the workplace is not found.
     */
    @Operation(
            summary = "Update workplace",
            description = "Update an existing workplace by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the workplace",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid workplace data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Workplace not found",
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
    public ResponseEntity<StandardApiResponse<WorkplaceResponse>> update(
            @Parameter(description = "ID of the workplace to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated workplace details", required = true)
            @RequestBody @Valid WorkplaceRequest.WithoutEmploymentInformationId workplaceRequest
    ) throws BadRequestException {
        log.info("Request to update workplace with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(workplaceService.update(id, workplaceRequest)));
    }

    /**
     * Deletes a specific workplace by its ID.
     *
     * @param id The ID of the workplace to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the workplace is not found.
     */
    @Operation(
            summary = "Delete workplace",
            description = "Delete a specific workplace by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the workplace",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Workplace not found",
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
            @Parameter(description = "ID of the workplace to delete", required = true)
            @PathVariable @NotNull String id
    ) throws NotFoundException {
        log.debug("Request to delete workplace with id [{}]", id);
        var isDeleted = workplaceService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Workplace with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}