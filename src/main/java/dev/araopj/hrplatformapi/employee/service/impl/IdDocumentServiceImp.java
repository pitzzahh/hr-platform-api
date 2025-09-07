package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.JsonRedactor;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentMapper;
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
    private final IdDocumentMapper idDocumentMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "identifierNumber", "employee");
    private final String ENTITY_NAME = IdDocumentResponse.class.getName();

    @Override
    public List<IdDocumentResponse> findAll() {
        var ID_DOCUMENTS = idDocumentRepository.findAll();
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(Map.of(
                        "timestamp", Instant.now().toString(),
                        "entity", ENTITY_NAME,
                        "count", ID_DOCUMENTS.size()
                )),
                Optional.empty(),
                Optional.empty(),
                "List<%s>".formatted(ENTITY_NAME)
        );
        return ID_DOCUMENTS.stream()
                .map(entity -> idDocumentMapper.toDto(entity, true))
                .toList();
    }

    @Override
    public Optional<IdDocumentResponse> findById(String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("id must be provided as path");
        }

        auditUtil.audit(
                id,
                ENTITY_NAME
        );

        return idDocumentRepository
                .findById(id)
                .map(e -> idDocumentMapper.toDto(e, true))
                .map(Optional::of)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));
    }

    @Override
    public IdDocumentResponse create(IdDocumentRequest request) {
        idDocumentRepository.findByIdentifierNumber(request.identifierNumber())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("IdDocument with identifierNumber [%s] already exists".formatted(request.identifierNumber()));
                });
        return idDocumentMapper.toDto(idDocumentRepository.save(idDocumentMapper.toEntity(request)), false);

    }

    @Override
    public IdDocumentResponse update(String id, IdDocumentRequest request) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("id must be provided as path");
        }

        final var EXISTING_IDENTIFIER = idDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));

        final var OLD_DTO = idDocumentMapper.toDto(EXISTING_IDENTIFIER, false);
        final var IDENTIFIER = MergeUtil.merge(EXISTING_IDENTIFIER, idDocumentMapper.toEntity(request));

        final var OLD_REDACTED = JsonRedactor.redact(EXISTING_IDENTIFIER, REDACTED);
        final var UPDATED_IDENTIFIER = idDocumentRepository.save(IDENTIFIER);
        final var NEW_DTO = idDocumentMapper.toDto(UPDATED_IDENTIFIER, false);

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
    public boolean delete(String id) throws BadRequestException {
        findById(id).orElseThrow();
        auditUtil.audit(
                DELETE,
                id,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        idDocumentRepository.deleteById(id);
        return true;
    }
}
