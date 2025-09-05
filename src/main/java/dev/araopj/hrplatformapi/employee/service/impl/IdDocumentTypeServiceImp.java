package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentTypeRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentTypeService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
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
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.IDENTIFIER;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.IDENTIFIER_TYPE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdDocumentTypeServiceImp implements IdDocumentTypeService {

    private final IdDocumentTypeRepository idDocumentTypeRepository;
    private final IdDocumentRepository idDocumentRepository;
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
                .map(Mapper::toDto)
                .toList();
    }

    public Optional<IdDocumentTypeResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(idDocumentTypeRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, IDENTIFIER_TYPE)));
    }

    @Override
    public IdDocumentTypeResponse create(IdDocumentTypeRequest idDocumentTypeRequest) {
        final var identifier_id = idDocumentTypeRequest.identifierId();

        var optionalIdentifier = idDocumentRepository.findById(identifier_id);

        if (optionalIdentifier.isEmpty()) {
            log.warn("Checking idDocument with path variable identifierId: {}", identifier_id);
            optionalIdentifier = idDocumentRepository.findById(identifier_id);
            if (optionalIdentifier.isEmpty()) {
                throw new NotFoundException(identifier_id, IDENTIFIER);
            }
        }

        final var SAVED_DATA = idDocumentTypeRepository.save(
                Mapper.toEntity(idDocumentTypeRequest)
        );

        auditUtil.audit(
                CREATE,
                String.valueOf(idDocumentTypeRequest.code()),
                Optional.empty(),
                redact(SAVED_DATA, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Mapper.toDto(SAVED_DATA);
    }
}
