package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.WorkplaceRepository;
import dev.araopj.hrplatformapi.employee.service.WorkplaceService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.mappers.WorkplaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.WORKPLACE;

/**
 * Implementation of {@link WorkplaceService} for managing workplace-related operations.
 * Provides functionality for retrieving, creating, updating, and deleting workplace records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkplaceServiceImp implements WorkplaceService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceMapper workplaceMapper;

    @Override
    public Page<WorkplaceResponse> findAll(Pageable pageable) {
        final var WORKPLACE_DATA = workplaceRepository.findAll(pageable);
        return WORKPLACE_DATA
                .map(e -> workplaceMapper.toDto(e, false));
    }

    @Override
    public Optional<WorkplaceResponse> findById(String id) throws NotFoundException {
        return Optional.ofNullable(workplaceRepository.findById(id)
                .map(e -> workplaceMapper.toDto(e, false))
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

        final var EXISTING_EMPLOYMENT_INFORMATION = employmentInformationRepository.findById(EMPLOYMENT_INFORMATION_ID)
                .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, EMPLOYMENT_INFORMATION));

        final var WORKPLACE_TO_SAVE = workplaceMapper.toEntity(workplaceRequest,
                EXISTING_EMPLOYMENT_INFORMATION
        );

        log.debug("Workplace to save [{}]", WORKPLACE_TO_SAVE);

        return workplaceMapper.toDto(workplaceRepository.save(WORKPLACE_TO_SAVE), false);
    }

    @Override
    public WorkplaceResponse update(
            String id,
            WorkplaceRequest.WithoutEmploymentInformationId workplaceRequest
    ) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Workplace ID must be provided as path");
        }
        return workplaceMapper.toDto(workplaceRepository.save(
                workplaceMapper.toEntity(workplaceRequest)
        ), false);

    }

    @Override
    public boolean delete(String id) throws NotFoundException {
        findById(id).orElseThrow();
        workplaceRepository.deleteById(id);
        return !workplaceRepository.existsById(id);
    }

}
