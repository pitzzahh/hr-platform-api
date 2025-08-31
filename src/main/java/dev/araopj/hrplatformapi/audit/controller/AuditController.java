package dev.araopj.hrplatformapi.audit.controller;

import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.ApiResponse;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
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
@RequestMapping("/audits")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Audit>>> all(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var auditsPage = auditService.findAll(PageRequest.of(page - 1, size));
        return ResponseEntity.ok(ApiResponse.success(auditsPage.getContent(), new PaginationMeta(
                auditsPage.getNumber() + 1,
                auditsPage.getSize(),
                auditsPage.getTotalElements(),
                auditsPage.getTotalPages()
        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Audit>> get(@PathVariable @NotNull String id) {
        return auditService.findById(id)
                .map(e -> ResponseEntity.ok(ApiResponse.success(e)))
                .orElseGet(() -> new ResponseEntity<>(ApiResponse.failure(
                                ApiError.builder()
                                        .message("Audit with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuditDto>> create(@RequestBody @Valid AuditDto audit) {
        log.debug("Request to create audit: {}", audit);
        return new ResponseEntity<>(ApiResponse.success(Mapper.toDto(auditService.create(audit))), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AuditDto>> update(@PathVariable @NotNull String id, @RequestBody @Valid AuditDto audit) {
        return auditService.update(id, audit)
                .map(e -> ResponseEntity.ok(ApiResponse.success(Mapper.toDto(e))))
                .orElseGet(() -> new ResponseEntity<>(ApiResponse.failure(
                                ApiError.builder()
                                        .message("Audit with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @NotNull String id) {
        var isDeleted = auditService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(ApiResponse.failure(
                    ApiError.builder()
                            .message("Audit with id [%s] not found".formatted(id))
                            .build()
            ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok().build();
    }
}

