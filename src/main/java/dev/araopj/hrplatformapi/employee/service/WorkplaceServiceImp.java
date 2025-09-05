package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.WorkplaceRepository;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.WORKPLACE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

/**
 * Implementation of {@link IWorkplaceService} for managing workplace-related operations.
 * Provides functionality for retrieving, creating, updating, and deleting workplace records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkplaceServiceImp implements IWorkplaceService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final WorkplaceRepository workplaceRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "employmentInformation");
    private final String ENTITY_NAME = WorkplaceResponse.class.getName();

    @Override
    public Page<WorkplaceResponse> findAll(Pageable pageable) {
        final var WORKPLACE_DATA = workplaceRepository.findAll(pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.empty(),
                PaginationMeta.builder()
                        .totalElements(WORKPLACE_DATA.getTotalElements())
                        .size(WORKPLACE_DATA.getSize())
                        .page(WORKPLACE_DATA.getNumber() + 1)
                        .totalPages(WORKPLACE_DATA.getTotalPages())
                        .build(),
                Optional.empty(),
                ENTITY_NAME
        );

        return WORKPLACE_DATA
                .map(Mapper::toDto);
    }

    @Override
    public Optional<WorkplaceResponse> findById(String id) throws NotFoundException {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.of(workplaceRepository.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, WORKPLACE)));
    }

    @Override
    public WorkplaceResponse create(WorkplaceRequest workplaceRequest) {
        final var EMPLOYMENT_INFORMATION_ID = workplaceRequest.employmentInformationId();
        workplaceRepository.findByCodeAndNameAndEmploymentInformationId(
                workplaceRequest.code(),
                workplaceRequest.name(),
                EMPLOYMENT_INFORMATION_ID
        ).ifPresent(workplace -> {
            throw new IllegalArgumentException("Workplace with code [%s] and name [%s] already exists for EmploymentInformation with id [%s]".formatted(
                    workplace.getCode(),
                    workplace.getName(),
                    EMPLOYMENT_INFORMATION_ID
            ));
        });

        final var WORKPLACE_TO_SAVE = Mapper.toEntity(workplaceRequest,
                employmentInformationRepository.findById(EMPLOYMENT_INFORMATION_ID)
                        .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, EMPLOYMENT_INFORMATION))
        );

        auditUtil.audit(
                CREATE,
                WORKPLACE_TO_SAVE.getId(),
                Optional.empty(),
                redact(WORKPLACE_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return Mapper.toDto(workplaceRepository.save(WORKPLACE_TO_SAVE));
    }

    @Override
    public WorkplaceResponse update(
            String id,
            WorkplaceRequest.WithoutEmploymentInformationId workplaceRequest
    ) throws BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Workplace ID must be provided as path");
        }

        final var ORIGINAL_WORKPLACE_DATA = workplaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, WORKPLACE));
        var WORKPLACE_DATA = MergeUtil.merge(ORIGINAL_WORKPLACE_DATA,
                Mapper.toEntity(workplaceRequest)
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_WORKPLACE_DATA, REDACTED)),
                redact(WORKPLACE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_WORKPLACE_DATA, WORKPLACE_DATA), REDACTED)),
                ENTITY_NAME
        );

        return Mapper.toDto(workplaceRepository.save(WORKPLACE_DATA));

    }

    @Override
    public boolean delete(String id) {
        var data = findById(id);
        if (data.isEmpty()) {
            log.warn("Workplace with id [{}] not found for deletion", id);
            return false;
        }

        if (!workplaceRepository.existsById(id)) {
            log.warn("Workplace with id [{}] not found for deletion", id);
            return false;
        }

        workplaceRepository.deleteById(id);

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
