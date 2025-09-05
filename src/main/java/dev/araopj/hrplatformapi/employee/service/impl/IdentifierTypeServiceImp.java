package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdentifierTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierTypeResponse;
import dev.araopj.hrplatformapi.employee.repository.IdentifierRepository;
import dev.araopj.hrplatformapi.employee.repository.IdentifierTypeRepository;
import dev.araopj.hrplatformapi.employee.service.IIdentifierTypeService;
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
public class IdentifierTypeServiceImp implements IIdentifierTypeService {

    private final IdentifierTypeRepository identifierTypeRepository;
    private final IdentifierRepository identifierRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "identifier");
    private final String ENTITY_NAME = "IdentifierTypeResponse";

    @Override
    public List<IdentifierTypeResponse> findAll() {
        var data = identifierTypeRepository.findAll()
                .stream()
                .map(Mapper::toDto)
                .toList();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", data.size()
                ),
                Optional.empty(),
                ENTITY_NAME
        );
        return data;
    }

    public Optional<IdentifierTypeResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(identifierTypeRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, IDENTIFIER_TYPE)));
    }

    @Override
    public IdentifierTypeResponse create(IdentifierTypeRequest identifierTypeRequest) {
        final var identifier_id = identifierTypeRequest.identifierId();

        var optionalIdentifier = identifierRepository.findById(identifier_id);

        if (optionalIdentifier.isEmpty()) {
            log.warn("Checking identifier with path variable identifierId: {}", identifier_id);
            optionalIdentifier = identifierRepository.findById(identifier_id);
            if (optionalIdentifier.isEmpty()) {
                throw new NotFoundException(identifier_id, IDENTIFIER);
            }
        }

        final var SAVED_DATA = identifierTypeRepository.save(
                Mapper.toEntity(identifierTypeRequest)
        );

        auditUtil.audit(
                CREATE,
                String.valueOf(identifierTypeRequest.code()),
                Optional.empty(),
                redact(SAVED_DATA, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Mapper.toDto(SAVED_DATA);
    }
}
