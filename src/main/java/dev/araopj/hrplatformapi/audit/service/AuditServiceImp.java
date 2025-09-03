package dev.araopj.hrplatformapi.audit.service;

import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.repository.AuditRepository;
import dev.araopj.hrplatformapi.utils.Mapper;
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
 * @see IAuditService
 * @see Audit
 * @see AuditRequest
 * @see AuditResponse
 * @see AuditRepository
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AuditServiceImp implements IAuditService {

    private final AuditRepository auditRepository;

    @Override
    public Page<AuditResponse> findAll(Pageable pageable) {
        log.debug("Retrieving audit records with pagination: {}", pageable);
        return auditRepository.findAll(pageable)
                .map(Mapper::toDto);
    }

    @Override
    public Optional<AuditResponse> findById(@NotNull String id) {
        log.debug("Retrieving audit record with ID: {}", id);
        return auditRepository.findById(id)
                .map(Mapper::toDto);
    }

    @Override
    public AuditResponse create(@NotNull AuditRequest request) {
        log.debug("Creating audit record: {}", request);

        // Validate audit data consistency
        boolean noChangesWhenNoOldData = request.oldData() == null && request.newData() != null;
        boolean noChangesWhenNoNewData = request.oldData() != null && request.newData() == null;
        if (noChangesWhenNoOldData || noChangesWhenNoNewData) {
            log.warn("Creating audit with inconsistent data: oldData={}, newData={}, changes={}",
                    request.oldData(), request.newData(), request.changes());
        }

        Audit audit = Mapper.toEntity(request);
        Audit savedAudit = auditRepository.saveAndFlush(audit);
        log.debug("Saved audit record with ID: {}", savedAudit.getId());

        return Mapper.toDto(savedAudit);
    }
}