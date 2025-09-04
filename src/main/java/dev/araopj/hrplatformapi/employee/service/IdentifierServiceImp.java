package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.IdentifierRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdentifierResponse;
import dev.araopj.hrplatformapi.employee.repository.IdentifierRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.VIEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentifierServiceImp implements IIdentifierService {

    private final IdentifierRepository identifierRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "", "employee");
    private final String ENTITY_NAME = "IdentifierResponse";

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
    public IdentifierResponse update(IdentifierRequest request) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }
}
