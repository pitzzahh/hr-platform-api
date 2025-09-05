package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.service.EmploymentInformationService;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the {@link EmploymentInformationService} interface.
 * <p>
 * This service provides methods for managing employment information records,
 * including retrieval, creation, updating, and deletion operations.
 *
 * @see EmploymentInformationService
 * @see EmploymentInformationResponse
 * @see EmploymentInformationRequest
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmploymentInformationServiceImp implements EmploymentInformationService {

    private final EmploymentInformationRepository employmentInformationRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("employeeResponse", "employmentInformationSalaryOverrideResponse", "positionResponse", "workplaceResponse");

    @Override
    public Page<EmploymentInformationResponse> findAll(Pageable pageable) {
        return employmentInformationRepository.findAll(pageable).map(employmentInformation -> {
            EmploymentInformationResponse response = EmploymentInformationResponse.fromEntity(employmentInformation);
            auditUtil.logEntityAccess(response, REDACTED);
            return response;
        });
    }

    @Override
    public Page<EmploymentInformationResponse> findByEmployeeId(String employeeId, Pageable pageable) {
        return null;
    }

    @Override
    public Optional<EmploymentInformationResponse> findById(String id) {
        return Optional.empty();
    }

    @Override
    public EmploymentInformationResponse create(String id, EmploymentInformationRequest employmentInformationRequest) {
        return null;
    }

    @Override
    public EmploymentInformationResponse update(String id, EmploymentInformationRequest employmentInformationRequest) {
        return null;
    }

    @Override
    public boolean delete(String id) {
        return false;
    }
}
