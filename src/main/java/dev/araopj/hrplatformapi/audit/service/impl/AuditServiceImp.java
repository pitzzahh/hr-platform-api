package dev.araopj.hrplatformapi.audit.service.impl;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.repository.AuditRepository;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.utils.mappers.AuditMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing audit records.
 * <p>
 * This class provides methods for retrieving and creating audit records, handling
 * validation, and mapping between entities and DTOs. It interacts with the
 * {@link AuditRepository} for data persistence.
 *
 * @see AuditService
 * @see Audit
 * @see AuditRequest
 * @see AuditResponse
 * @see AuditRepository
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuditServiceImp implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditMapper auditMapper;

    @Override
    public Page<AuditResponse> findAll(Pageable pageable) {
        log.debug("Retrieving audit records with pagination: {}", pageable);
        return auditRepository.findAll(pageable)
                .map(auditMapper::toDto);
    }

    @Override
    public Optional<AuditResponse> findById(@NotNull String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        log.debug("Retrieving audit record with ID: {}", id);
        return auditRepository.findById(id)
                .map(auditMapper::toDto);
    }

    @Override
    public AuditResponse create(@NotNull AuditRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AuditRequest cannot be null");
        }
        log.debug("Creating audit record: {}", request);
        return auditMapper.toDto(auditRepository.save(auditMapper.toEntity(request)));
    }
}