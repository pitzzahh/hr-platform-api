package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.service.IdDocumentTypeService;
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
 * REST controller for managing ID document types.
 * Provides endpoints for retrieving, creating, updating, and deleting ID document type entries.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/id-document-types")
@RequiredArgsConstructor
@Tag(
        name = "ID Document Types",
        description = "Endpoints for managing ID document types."
)
public class IdDocumentTypeController {

    private final IdDocumentTypeService idDocumentTypeService;

    /**
     * Retrieves a list of all ID document types.
     *
     * @return A ResponseEntity containing a StandardApiResponse with a list of IdDocumentTypeResponse.
     */
    @Operation(
            summary = "Get all ID document types",
            description = "Retrieve a list of all ID document type entries.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of ID document types",
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
    public ResponseEntity<StandardApiResponse<List<IdDocumentTypeResponse>>> all() {
        log.debug("Fetching all ID document types");
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentTypeService.findAll()));
    }

    /**
     * Retrieves a specific ID document type by its ID.
     *
     * @param id The ID of the ID document type to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the IdDocumentTypeResponse.
     * @throws NotFoundException If the ID document type is not found.
     */
    @Operation(
            summary = "Get ID document type by ID",
            description = "Retrieve a specific ID document type by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the ID document type",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "ID document type not found",
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
    public ResponseEntity<StandardApiResponse<IdDocumentTypeResponse>> get(
            @Parameter(description = "ID of the ID document type to retrieve", required = true)
            @PathVariable String id
    ) {
        log.debug("Fetching ID document type with id: {}", id);
        var response = idDocumentTypeService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new ID document type.
     *
     * @param idDocumentTypeRequest The details of the ID document type to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created IdDocumentTypeResponse.
     * @throws NotFoundException If the associated ID document is not found.
     */
    @Operation(
            summary = "Create ID document type",
            description = "Create a new ID document type entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the ID document type",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID document type provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Associated ID document not found",
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
    public ResponseEntity<StandardApiResponse<IdDocumentTypeResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "ID document type details to create", required = true)
            IdDocumentTypeRequest idDocumentTypeRequest
    ) {
        log.debug("Request to create ID document type: {}", idDocumentTypeRequest);
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentTypeService.create(idDocumentTypeRequest)));
    }

    /**
     * Updates an existing ID document type by its ID.
     *
     * @param id                    The ID of the ID document type to update.
     * @param idDocumentTypeRequest The updated ID document type details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated IdDocumentTypeResponse.
     * @throws BadRequestException If invalid data is provided.
     * @throws NotFoundException   If the ID document type is not found.
     */
    @Operation(
            summary = "Update ID document type",
            description = "Update an existing ID document type by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the ID document type",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID document type provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "ID document type not found",
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
    public ResponseEntity<StandardApiResponse<IdDocumentTypeResponse>> update(
            @Parameter(description = "ID of the ID document type to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated ID document type details", required = true)
            @RequestBody @Valid IdDocumentTypeRequest idDocumentTypeRequest
    ) throws BadRequestException {
        log.info("Request to update ID document type with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentTypeService.update(id, idDocumentTypeRequest)));
    }

    /**
     * Deletes a specific ID document type by its ID.
     *
     * @param id The ID of the ID document type to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws NotFoundException If the ID document type is not found.
     */
    @Operation(
            summary = "Delete ID document type",
            description = "Delete a specific ID document type by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the ID document type",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "ID document type not found",
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
            @Parameter(description = "ID of the ID document type to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Request to delete ID document type with id [{}]", id);
        var isDeleted = idDocumentTypeService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("ID document type with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}