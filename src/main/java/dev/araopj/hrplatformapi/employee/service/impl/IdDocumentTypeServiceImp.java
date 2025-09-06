package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentTypeRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentTypeService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT_TYPE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Service class for managing {@link IdDocumentType} entities.
 * Provides methods to create, read, update, and delete IdDocumentType records.
 * Integrates with AuditUtil to log actions performed on these records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdDocumentTypeServiceImp implements IdDocumentTypeService {

    private final IdDocumentTypeRepository idDocumentTypeRepository;
    private final IdDocumentRepository idDocumentRepository;
    private final IdDocumentTypeMapper idDocumentTypeMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "idDocument");
    private final String ENTITY_NAME = IdDocumentTypeResponse.class.getName();

    @Override
    public List<IdDocumentTypeResponse> findAll() {
        final var DATA = idDocumentTypeRepository.findAll();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", DATA.size()
                )),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return DATA.stream()
                .map(e -> idDocumentTypeMapper.toDto(e, true))
                .toList();
    }

    @Override
    public Optional<IdDocumentTypeResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(idDocumentTypeRepository.findById(id)
                .map(e -> idDocumentTypeMapper.toDto(e, true))
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT_TYPE)));
    }

    @Override
    public IdDocumentTypeResponse create(IdDocumentTypeRequest idDocumentTypeRequest) {
        final var identifierId = idDocumentTypeRequest.identifierId();

        idDocumentRepository
                .findById(identifierId)
                .orElseThrow(() -> new NotFoundException(identifierId, ID_DOCUMENT));

        final var SAVED_DATA = idDocumentTypeRepository.save(
                idDocumentTypeMapper.toEntity(idDocumentTypeRequest)
        );

        auditUtil.audit(
                CREATE,
                String.valueOf(idDocumentTypeRequest.code()),
                Optional.empty(),
                redact(SAVED_DATA, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return idDocumentTypeMapper.toDto(SAVED_DATA, false);
    }

    @Override
    public IdDocumentTypeResponse update(String id, IdDocumentTypeRequest idDocumentTypeRequest) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("IdDocumentType ID must be provided as path");
        }

        final var ORIGINAL_ID_DOCUMENT_TYPE = findById(id).orElseThrow();

        var ID_DOCUMENT_TYPE_DATA = MergeUtil.merge(ORIGINAL_ID_DOCUMENT_TYPE,
                idDocumentTypeMapper.toEntity(idDocumentTypeRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_ID_DOCUMENT_TYPE, REDACTED)),
                redact(ID_DOCUMENT_TYPE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_ID_DOCUMENT_TYPE, ID_DOCUMENT_TYPE_DATA), REDACTED)),
                ENTITY_NAME
        );

        return idDocumentTypeMapper.toDto(idDocumentTypeRepository.save(
                idDocumentTypeMapper.toEntity(idDocumentTypeRequest)
        ), false);
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        idDocumentTypeRepository.deleteById(id);
        auditUtil.audit(
                DELETE,
                id,
                Optional.of(Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "id", id
                )),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return true;
    }
}