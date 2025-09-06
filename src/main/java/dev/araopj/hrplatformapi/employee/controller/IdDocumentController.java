package dev.araopj.hrplatformapi.employee.controller;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.service.IdDocumentService;
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
 * REST controller for managing ID documents.
 * Provides endpoints for retrieving, creating, updating, and deleting ID document entries.
 */
@Slf4j
@RestController
@RequestMapping("api/v1/id-documents")
@RequiredArgsConstructor
@Tag(
        name = "ID Documents",
        description = "Endpoints for managing ID document entries."
)
public class IdDocumentController {

    private final IdDocumentService idDocumentService;

    /**
     * Retrieves a list of all ID documents.
     *
     * @return A ResponseEntity containing a StandardApiResponse with a list of IdDocumentResponse.
     */
    @Operation(
            summary = "Get all ID documents",
            description = "Retrieve a list of all ID document entries.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the list of ID documents",
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
    public ResponseEntity<StandardApiResponse<List<IdDocumentResponse>>> all() {
        log.debug("Fetching all ID documents");
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentService.findAll()));
    }

    /**
     * Retrieves a specific ID document by its ID.
     *
     * @param id The ID of the ID document to retrieve.
     * @return A ResponseEntity containing a StandardApiResponse with the IdDocumentResponse.
     * @throws NotFoundException If the ID document is not found.
     */
    @Operation(
            summary = "Get ID document by ID",
            description = "Retrieve a specific ID document by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the ID document",
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
                            description = "ID document not found",
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
    public ResponseEntity<StandardApiResponse<IdDocumentResponse>> get(
            @Parameter(description = "ID of the ID document to retrieve", required = true)
            @PathVariable String id
    ) throws BadRequestException {
        log.debug("Fetching ID document with id: {}", id);
        var response = idDocumentService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow();
    }

    /**
     * Creates a new ID document.
     *
     * @param idDocumentRequest The details of the ID document to create.
     * @return A ResponseEntity containing a StandardApiResponse with the created IdDocumentResponse.
     * @throws IllegalArgumentException If the identifier number already exists.
     */
    @Operation(
            summary = "Create ID document",
            description = "Create a new ID document entry.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the ID document",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID document provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "ID document with the same identifier number already exists",
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
    public ResponseEntity<StandardApiResponse<IdDocumentResponse>> create(
            @Valid
            @RequestBody
            @Parameter(description = "ID document details to create", required = true)
            IdDocumentRequest idDocumentRequest
    ) {
        log.debug("Request to create ID document: {}", idDocumentRequest);
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentService.create(idDocumentRequest)));
    }

    /**
     * Updates an existing ID document by its ID.
     *
     * @param id                The ID of the ID document to update.
     * @param idDocumentRequest The updated ID document details.
     * @return A ResponseEntity containing a StandardApiResponse with the updated IdDocumentResponse.
     * @throws BadRequestException If invalid data or ID is provided.
     * @throws NotFoundException   If the ID document is not found.
     */
    @Operation(
            summary = "Update ID document",
            description = "Update an existing ID document by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the ID document",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID document provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "ID document not found",
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
    public ResponseEntity<StandardApiResponse<IdDocumentResponse>> update(
            @Parameter(description = "ID of the ID document to update", required = true)
            @PathVariable @NotNull String id,
            @Parameter(description = "Updated ID document details", required = true)
            @RequestBody @Valid IdDocumentRequest idDocumentRequest
    ) throws BadRequestException {
        log.info("Request to update ID document with id [{}]", id);
        return ResponseEntity.ok(StandardApiResponse.success(idDocumentService.update(id, idDocumentRequest)));
    }

    /**
     * Deletes a specific ID document by its ID.
     *
     * @param id The ID of the ID document to delete.
     * @return A ResponseEntity indicating success or failure of the deletion.
     * @throws BadRequestException If invalid ID is provided.
     * @throws NotFoundException   If the ID document is not found.
     */
    @Operation(
            summary = "Delete ID document",
            description = "Delete a specific ID document by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the ID document",
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
                            description = "ID document not found",
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
            @Parameter(description = "ID of the ID document to delete", required = true)
            @PathVariable @NotNull String id
    ) throws BadRequestException {
        log.debug("Request to delete ID document with id [{}]", id);
        var isDeleted = idDocumentService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("ID document with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok(StandardApiResponse.success(null));
    }
}