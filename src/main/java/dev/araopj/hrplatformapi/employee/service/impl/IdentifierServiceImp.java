package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdentifierRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierResponse;
import dev.araopj.hrplatformapi.employee.repository.IdentifierRepository;
import dev.araopj.hrplatformapi.employee.service.IdentifierService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.JsonRedactor;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
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
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.IDENTIFIER;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentifierServiceImp implements IdentifierService {

    private final IdentifierRepository identifierRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "identifierNumber", "employee");
    private final String ENTITY_NAME = IdentifierResponse.class.getName();

    @Override
    public List<IdentifierResponse> findAll() {
        var IDENTIFIERS = identifierRepository.findAll();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", IDENTIFIERS.size()
                )),
                Optional.empty(),
                Optional.empty(),
                "List<%s>".formatted(ENTITY_NAME)
        );
        return List.of();
    }

    @Override
    public Optional<IdentifierResponse> findById(String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Identifier ID must be provided as path");
        }

        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        return identifierRepository
                .findById(id)
                .map(Mapper::toDto)
                .map(Optional::of)
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.IDENTIFIER));
    }

    @Override
    public IdentifierResponse create(IdentifierRequest request) {
        identifierRepository.findByIdentifierNumber(request.identifierNumber())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Identifier with number %s already exists".formatted(request.identifierNumber()));
                });
        return Mapper.toDto(identifierRepository.save(Mapper.toEntity(request)));

    }

    @Override
    public IdentifierResponse update(String id, IdentifierRequest request) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Identifier ID must be provided as path");
        }

        final var EXISTING_IDENTIFIER = identifierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, NotFoundException.EntityType.IDENTIFIER));

        final var OLD_DTO = Mapper.toDto(EXISTING_IDENTIFIER);
        final var IDENTIFIER = MergeUtil.merge(EXISTING_IDENTIFIER, Mapper.toEntity(request));

        final var OLD_REDACTED = JsonRedactor.redact(EXISTING_IDENTIFIER, REDACTED);
        final var UPDATED_IDENTIFIER = identifierRepository.save(IDENTIFIER);
        final var NEW_DTO = Mapper.toDto(UPDATED_IDENTIFIER, false);

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(OLD_REDACTED),
                redact(UPDATED_IDENTIFIER, REDACTED),
                Optional.of(redact(diff(OLD_DTO, NEW_DTO), REDACTED)),
                ENTITY_NAME
        );

        return NEW_DTO;
    }

    @Override
    public boolean deleteById(String id) {
        final var IDENTIFIER_TO_BE_REMOVED = identifierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, IDENTIFIER));

        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(Mapper.toDto(IDENTIFIER_TO_BE_REMOVED, false), REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        identifierRepository.deleteById(id);
        return true;
    }
}
