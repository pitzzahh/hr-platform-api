package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.service.PositionService;
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
 * REST controller for managing position data.
 * Provides endpoints for retrieving, creating, updating, and deleting position entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/positions")
@RequiredArgsConstructor
@Tag(
        name = "Positions",
        description = "Endpoints for managing position data."
)
public class PositionController {

    private final PositionService positionService;

    /**
     * Retrieves a paginated list of all position entries.
     *
     * @param page The page number (1-based).
     * @param size The number of records per page.
     * @return A ResponseEntity containing a StandardApiResponse with a list of PositionResponse and pagination metadata.
     * @throws BadRequestException If invalid parameters are provided.
     */
    @Operation(
            summary = "Get all positions",
            description = "Retrieve a paginated list of all position entries. Supports pagination through 'page' and 'size' query parameters.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of positions",
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
    public ResponseEntity<StandardApiResponse<List<PositionResponse>>> all(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) throws BadRequestException {
        log.debug("Fetching all positions with page: {} and size: {}", page, size);
        final var PAGE = positionService.findAll(PageRequest.of(page - 1, size));
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
     * Retrieves a specific position by its ID.
     *
     * @param id The ID of the position to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the PositionResponse.
     * @throws NotFoundException If the position is not found.
     */
    @Operation(
            summary = "Get position by ID",
            description = "Retrieve a specific position by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the position",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Position not found",
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
    public ResponseEntity<StandardApiResponse<PositionResponse>> get(
            @Parameter(description = "ID of the position to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Fetching position with id: {}", id);
        var response = positionService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new position.
     *
     * @param positionRequest The details of the position to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created PositionResponse.
     * @throws IllegalArgumentException If the position code already exists for the employment information.
     */
    @Operation(
            summary = "Create position",
            description = "Create a new position entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the position",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid position data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Position with the same code already exists for the employment information",
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
    public ResponseEntity<StandardApiResponse<PositionResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "Position details to create", required = true)
            PositionRequest positionRequest
    ) {
        log.debug("Request to create position: {}", positionRequest);
        return ResponseEntity.ok(StandardApiResponse.success(positionService.create(positionRequest)));
    }

    /**
     * Updates an existing position by its ID.
     *
     * @param id              The ID of the position to update.
     * @param positionRequest The updated position details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated PositionResponse.
     * @throws BadRequestException If invalid data or ID is provided.
     * @throws NotFoundException   If the position is not found.
     */
    @Operation(
            summary = "Update position",
            description = "Update an existing position by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the position",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid position data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Position not found",
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
    public ResponseEntity<StandardApiResponse<PositionResponse>> update(
            @Parameter(description = "ID of the position to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated position details", required = true)
            @RequestBody @Valid PositionRequest.WithoutEmploymentInformationId positionRequest
    ) throws BadRequestException {
        log.info("Request to update position with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(positionService.update(id, positionRequest)));
    }

    /**
     * Deletes a specific position by its ID.
     *
     * @param id The ID of the position to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the position is not found.
     */
    @Operation(
            summary = "Delete position",
            description = "Delete a specific position by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the position",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Position not found",
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
            @Parameter(description = "ID of the position to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Request to delete position with id [{}]", id);
        var isDeleted = positionService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("Position with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}