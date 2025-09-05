package dev.araopj.hrplatformapi.audit.controller;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing audit records in the HR platform.
 * This controller provides endpoints for retrieving and creating audit records,
 * supporting pagination and validation. All responses are wrapped in a
 * {@link StandardApiResponse} for consistent API output.
 *
 * @see AuditService
 * @see AuditRequest
 * @see AuditResponse
 * @see StandardApiResponse
 */
@Slf4j
@RestController
@RequestMapping("api/v1/audits")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Endpoints for managing audit records, including retrieval and creation.")
public class AuditController {

    private final AuditService auditService;

    /**
     * Retrieves a paginated list of all audit records.
     * This endpoint supports pagination through query parameters for page number and size.
     * The response includes audit records and pagination metadata.
     *
     * @param page the page number (1-based, default: 1)
     * @param size the number of records per page (default: 10)
     * @return a {@link ResponseEntity} containing a {@link StandardApiResponse} with a list of
     * {@link AuditResponse} objects and pagination metadata
     */
    @GetMapping
    @Operation(
            summary = "Get all audit records with pagination",
            description = "Retrieves a paginated list of all audit records, including pagination metadata."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of audit records"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardApiResponse<List<AuditResponse>>> findAll(
            @Parameter(description = "Page number (1-based)", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Number of records per page", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
        log.debug("Retrieving audit records with page={} and size={}", page, size);
        Page<AuditResponse> auditsPage = auditService.findAll(PageRequest.of(page - 1, size));
        return ResponseEntity.ok(StandardApiResponse.success(
                auditsPage.getContent(),
                PaginationMeta.builder()
                        .page(auditsPage.getNumber() + 1)
                        .size(auditsPage.getSize())
                        .totalElements(auditsPage.getTotalElements())
                        .totalPages(auditsPage.getTotalPages())
                        .build()
        ));
    }

    /**
     * Retrieves a specific audit record by its ID.
     * If the audit record is not found, a 404 response is returned with an error message.
     *
     * @param id the unique ID of the audit record
     * @return a {@link ResponseEntity} containing a {@link StandardApiResponse} with the
     * {@link AuditResponse} object or an error if not found
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get audit record by ID",
            description = "Retrieves a specific audit record by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the audit record"),
            @ApiResponse(responseCode = "404", description = "Audit record not found"),
            @ApiResponse(responseCode = "400", description = "Invalid ID provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardApiResponse<AuditResponse>> findById(
            @Parameter(description = "Unique ID of the audit record", required = true)
            @PathVariable @NotNull String id
    ) {
        log.debug("Retrieving audit record with ID: {}", id);
        return auditService.findById(id)
                .map(audit -> ResponseEntity.ok(StandardApiResponse.success(audit)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("Audit record with ID [%s] not found".formatted(id))
                                        .build()
                        )));
    }

    /**
     * Creates a new audit record.
     * The request is validated, and the created audit record is returned with a 201 status.
     *
     * @param request the {@link AuditRequest} containing audit details
     * @return a {@link ResponseEntity} containing a {@link StandardApiResponse} with the
     * created {@link AuditResponse} object
     */
    @PostMapping
    @Operation(
            summary = "Create a new audit record",
            description = "Creates a new audit record based on the provided request data."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Audit record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid audit data provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<StandardApiResponse<AuditResponse>> create(
            @Parameter(description = "Audit request data", required = true)
            @RequestBody @Valid AuditRequest request
    ) {
        log.debug("Creating audit record: {}", request);
        AuditResponse auditResponse = auditService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardApiResponse.success(auditResponse));
    }
}