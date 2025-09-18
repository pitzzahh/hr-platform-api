package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.PositionRepository;
import dev.araopj.hrplatformapi.employee.service.PositionService;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.PositionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.POSITION;

/**
 * Implementation of {@link PositionService} for managing position-related operations.
 * Provides functionality for retrieving, creating, updating, and deleting position records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImp implements PositionService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final PositionRepository positionRepository;

    @Override
    public Page<PositionResponse> findAll(Pageable pageable) {
        final var POSITION_DATA = positionRepository.findAll(pageable);

        return POSITION_DATA
                .map(PositionMapper::toDto);
    }

    @Override
    public Optional<PositionResponse> findById(String id) {
        return Optional.ofNullable(positionRepository.findById(id)
                .map(PositionMapper::toDto)
                .orElseThrow(() -> new NotFoundException(id, POSITION)));
    }

    @Override
    public PositionResponse create(PositionRequest positionRequest) {

        final var EMPLOYMENT_INFORMATION_ID = positionRequest.employmentInformationId();

        positionRepository.findByCodeAndEmploymentInformationId(
                positionRequest.code(),
                EMPLOYMENT_INFORMATION_ID
        ).ifPresent(position -> {
            throw new IllegalArgumentException("Position with code [%s] already exists for EmploymentInformation with id [%s]".formatted(
                    position.getCode(),
                    EMPLOYMENT_INFORMATION_ID
            ));
        });

        final var POSITION_TO_SAVE = PositionMapper.toEntity(positionRequest,
                employmentInformationRepository.findById(EMPLOYMENT_INFORMATION_ID)
                        .orElseThrow(() -> new NotFoundException(EMPLOYMENT_INFORMATION_ID, EMPLOYMENT_INFORMATION))
        );

        return PositionMapper.toDto(positionRepository.save(POSITION_TO_SAVE));
    }

    @Override
    public PositionResponse update(String id, PositionRequest positionRequest) throws InvalidRequestException {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("Position ID must be provided as path");
        }

        final var ORIGINAL_POSITION_DATA = positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, POSITION));
        var POSITION_DATA = MergeUtil.merge(ORIGINAL_POSITION_DATA,
                PositionMapper.toEntity(positionRequest)
        );

        return PositionMapper.toDto(positionRepository.save(POSITION_DATA));
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        positionRepository.deleteById(id);
        return !positionRepository.existsById(id);
    }
}
