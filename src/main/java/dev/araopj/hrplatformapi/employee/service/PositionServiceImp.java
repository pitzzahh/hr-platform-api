package dev.araopj.hrplatformapi.employee.service;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.model.Position;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.PositionRepository;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public final class PositionServiceImp implements IPositionService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final PositionRepository repository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("id", "employmentInformation");
    private final String ENTITY_NAME = PositionResponse.class.getName();

    @Override
    public Page<Position> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Position> findById(String id) {
        return Optional.empty();
    }

    @Override
    public PositionResponse create(PositionRequest positionRequest) {
        return null;
    }

    @Override
    public PositionResponse update(PositionRequest positionRequest) {
        return null;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
