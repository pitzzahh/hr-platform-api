package dev.araopj.hrplatformapi.audit.controller;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/audits")
@RequiredArgsConstructor
@Tag(
        name = "Audit",
        description = "Operations related to audit records, including creation, retrieval, update, and deletion."
)
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(
            description = "Retrieve a paginated list of all audit records.",
            summary = "Get all audits with pagination",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of audits"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid pagination parameters provided"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<List<Audit>>> all(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var auditsPage = auditService.findAll(PageRequest.of(page - 1, size));
        return ResponseEntity.ok(StandardApiResponse.success(auditsPage.getContent(), new PaginationMeta(
                auditsPage.getNumber() + 1,
                auditsPage.getSize(),
                auditsPage.getTotalElements(),
                auditsPage.getTotalPages()
        )));
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Retrieve a specific audit record by its ID.",
            summary = "Get audit by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the audit record"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Audit record not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<Audit>> get(@PathVariable @NotNull String id) {
        return auditService.findById(id)
                .map(e -> ResponseEntity.ok(StandardApiResponse.success(e)))
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("Audit with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @PostMapping
    @Operation(
            description = "Create a new audit record.",
            summary = "Create audit",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Audit record created successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid audit data provided"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<AuditDto>> create(@RequestBody @Valid AuditDto audit) {
        log.debug("Request to create audit: {}", audit);
        return new ResponseEntity<>(StandardApiResponse.success(Mapper.toDto(auditService.create(audit))), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Update an existing audit record by its ID.",
            summary = "Update audit",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Audit record updated successfully"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid audit data provided"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Audit record not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<AuditDto>> update(@PathVariable @NotNull String id, @RequestBody @Valid AuditDto audit) {
        return auditService.update(id, audit)
                .map(e -> ResponseEntity.ok(StandardApiResponse.success(Mapper.toDto(e))))
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("Audit with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete an audit record by its ID.",
            summary = "Delete audit",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Audit record deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Audit record not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<Void>> delete(@PathVariable @NotNull String id) {
        var isDeleted = auditService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(StandardApiResponse.failure(
                    ApiError.builder()
                            .message("Audit with id [%s] not found".formatted(id))
                            .build()
            ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok().build();
    }
}

