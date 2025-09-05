package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentService;
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
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;
import static dev.araopj.hrplatformapi.utils.DiffUtil.diff;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdDocumentServiceImp implements IdDocumentService {

    private final IdDocumentRepository idDocumentRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "identifierNumber", "employee");
    private final String ENTITY_NAME = IdDocumentResponse.class.getName();

    @Override
    public List<IdDocumentResponse> findAll() {
        var IDENTIFIERS = idDocumentRepository.findAll();
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
    public Optional<IdDocumentResponse> findById(String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("IdDocument ID must be provided as path");
        }

        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        return idDocumentRepository
                .findById(id)
                .map(Mapper::toDto)
                .map(Optional::of)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));
    }

    @Override
    public IdDocumentResponse create(IdDocumentRequest request) {
        idDocumentRepository.findByIdentifierNumber(request.identifierNumber())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("IdDocument with number %s already exists".formatted(request.identifierNumber()));
                });
        return Mapper.toDto(idDocumentRepository.save(Mapper.toEntity(request)));

    }

    @Override
    public IdDocumentResponse update(String id, IdDocumentRequest request) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("IdDocument ID must be provided as path");
        }

        final var EXISTING_IDENTIFIER = idDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));

        final var OLD_DTO = Mapper.toDto(EXISTING_IDENTIFIER);
        final var IDENTIFIER = MergeUtil.merge(EXISTING_IDENTIFIER, Mapper.toEntity(request));

        final var OLD_REDACTED = JsonRedactor.redact(EXISTING_IDENTIFIER, REDACTED);
        final var UPDATED_IDENTIFIER = idDocumentRepository.save(IDENTIFIER);
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
        final var IDENTIFIER_TO_BE_REMOVED = idDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));

        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(Mapper.toDto(IDENTIFIER_TO_BE_REMOVED, false), REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        idDocumentRepository.deleteById(id);
        return true;
    }
}
