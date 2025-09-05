package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentTypeRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentTypeService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.CREATE;
import static dev.araopj.hrplatformapi.audit.model.AuditAction.VIEW;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT_TYPE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Service class for managing {@link IdDocumentType} entities.
 * <p>
 * This service provides methods to create, read, and list IdDocumentType records.
 * It also integrates with the AuditUtil to log actions performed on these records.
 *
 * @see IdDocumentTypeRepository
 * @see IdDocumentTypeMapper
 * @see AuditUtil
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
    private final String ENTITY_NAME = IdDocumentResponse.class.getName();

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
        final var identifier_id = idDocumentTypeRequest.identifierId();

        var optionalIdentifier = idDocumentRepository.findById(identifier_id);

        if (optionalIdentifier.isEmpty()) {
            log.warn("Checking idDocument with path variable identifierId: {}", identifier_id);
            optionalIdentifier = idDocumentRepository.findById(identifier_id);
            if (optionalIdentifier.isEmpty()) {
                throw new NotFoundException(identifier_id, ID_DOCUMENT);
            }
        }

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
}
