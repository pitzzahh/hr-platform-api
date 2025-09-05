package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.PositionRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
import dev.araopj.hrplatformapi.utils.mappers.PositionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.POSITION;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Implementation of {@link IPositionService} for managing position-related operations.
 * Provides functionality for retrieving, creating, updating, and deleting position records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public final class PositionServiceImp implements IPositionService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "employmentInformation");
    private final String ENTITY_NAME = PositionResponse.class.getName();

    @Override
    public Page<PositionResponse> findAll(Pageable pageable) {
        final var POSITION_DATA = positionRepository.findAll(pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                PaginationMeta.builder()
                        .totalElements(POSITION_DATA.getTotalElements())
                        .size(POSITION_DATA.getSize())
                        .page(POSITION_DATA.getNumber() + 1)
                        .totalPages(POSITION_DATA.getTotalPages())
                        .build(),
                Optional.empty(),
                ENTITY_NAME
        );

        return POSITION_DATA
                .map(positionMapper::toDto);
    }

    @Override
    public Optional<PositionResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(positionRepository.findById(id)
                .map(positionMapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, POSITION)));
    }

    @Override
    public PositionResponse create(PositionRequest positionRequest) {

        final var EMPLOYMENT_INFORMATION_ID = positionRequest.employmentInformationId();

        positionRepository.findByCodeAndEmploymentInformationId(
                positionRequest.code(),
                positionRequest.description(),
                EMPLOYMENT_INFORMATION_ID
        ).ifPresent(position -> {
            throw new IllegalArgumentException("Position with code [%s] already exists for EmploymentInformation with id [%s]".formatted(
                    position.getCode(),
                    EMPLOYMENT_INFORMATION_ID
            ));
        });

        final var POSITION_TO_SAVE = positionMapper.toEntity(positionRequest,
                employmentInformationRepository.findById(EMPLOYMENT_INFORMATION_ID)
                        .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, POSITION))
        );

        auditUtil.audit(
                CREATE,
                POSITION_TO_SAVE.getId(),
                Optional.empty(),
                redact(POSITION_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return positionMapper.toDto(positionRepository.save(POSITION_TO_SAVE));
    }

    @Override
    public PositionResponse update(String id, PositionRequest.WithoutEmploymentInformationId positionRequest) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Position ID must be provided as path");
        }

        final var ORIGINAL_POSITION_DATA = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, POSITION));
        var POSITION_DATA = MergeUtil.merge(ORIGINAL_POSITION_DATA,
                positionMapper.toEntity(positionRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_POSITION_DATA, REDACTED)),
                redact(POSITION_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_POSITION_DATA, POSITION_DATA), REDACTED)),
                ENTITY_NAME
        );

        return positionMapper.toDto(positionRepository.save(POSITION_DATA));
    }

    @Override
    public boolean delete(String id) {
        var data = findById(id);
        if (data.isEmpty()) {
            log.warn("Position with id [{}] not found for deletion", id);
            return false;
        }

        if (!positionRepository.existsById(id)) {
            log.warn("Position with id [{}] not found for deletion", id);
            return false;
        }

        positionRepository.deleteById(id);

        auditUtil.audit(
                DELETE,
                id,
                Optional.of(redact(data, REDACTED)),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        return true;
    }
}
