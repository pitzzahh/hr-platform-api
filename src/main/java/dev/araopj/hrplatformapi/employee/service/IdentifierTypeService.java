package dev.araopj.hrplatformapi.employee.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.employee.dto.request.IdentifierTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdentifierType;
import dev.araopj.hrplatformapi.employee.repository.IdentifierRepository;
import dev.araopj.hrplatformapi.employee.repository.IdentifierTypeRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
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
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.IDENTIFIER_TYPE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentifierTypeService {

    private final IdentifierTypeRepository identifierTypeRepository;
    private final IdentifierRepository identifierRepository;
    private final ObjectMapper objectMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "identifier");
    private final String ENTITY_NAME = "IdentifierTypeResponse";

    public List<IdentifierTypeResponse> findAll() {
        var data = identifierTypeRepository.findAll().stream()
                .map(entity -> objectMapper.convertValue(entity, IdentifierTypeResponse.class))
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
                .map(e -> objectMapper.convertValue(e, IdentifierTypeResponse.class))
                .orElseThrow(() -> new NotFoundException(id, IDENTIFIER_TYPE)));
    }

    public Optional<IdentifierTypeResponse> create(IdentifierTypeRequest identifierTypeRequest, String identifierId) {
        var identifier_id = identifierTypeRequest.identifierId();

        var optionalIdentifier = identifierRepository.findById(identifier_id);

        if (optionalIdentifier.isEmpty()) {
            log.warn("Checking identifier with path variable identifierId: {}", identifierId);
            optionalIdentifier = identifierRepository.findById(identifierId);
            if (optionalIdentifier.isEmpty()) {
                throw new NotFoundException(identifierId, IDENTIFIER_TYPE);
            }
        }

        var data = objectMapper.convertValue(identifierTypeRepository.saveAndFlush(
                objectMapper.convertValue(identifierTypeRequest, IdentifierType.class)
        ), IdentifierTypeResponse.class);

        auditUtil.audit(
                CREATE,
                String.valueOf(identifierTypeRequest.code()),
                Optional.empty(),
                redact(data, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );
        return Optional.of(data);
    }
}
