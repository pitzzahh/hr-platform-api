package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.employee.service.EmployeeService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.EmployeeMapper;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationMapper;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationSalaryOverrideMapper;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYEE;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmploymentInformationMapper employmentInformationMapper;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;
    private final IdDocumentMapper idDocumentMapper;

    @Override
    public Page<EmployeeResponse> findAll(Pageable pageable, boolean includeIdDocuments, boolean includeEmploymentInformation) {
        final var PAGINATED_DATA = includeIdDocuments && includeEmploymentInformation ?
                employeeRepository.findAllWithIdDocumentsAndEmploymentInformation(pageable) :
                includeIdDocuments ?
                        employeeRepository.findAllWithIdDocuments(pageable) :
                        includeEmploymentInformation ?
                                employeeRepository.findAllWithEmploymentInformation(pageable) :
                                employeeRepository.findAll(pageable);
        return PAGINATED_DATA
                .map(e -> employeeMapper.toDto(
                        e,
                        includeIdDocuments,
                        includeEmploymentInformation,
                        idDocumentMapper,
                        employmentInformationMapper,
                        employmentInformationSalaryOverrideMapper
                ));
    }

    @Override
    public Optional<EmployeeResponse> findById(String id, boolean includeIdDocuments, boolean includeEmploymentInformation) {
        return Optional.ofNullable(employeeRepository.findById(id)
                .map(e -> employeeMapper.toDto(
                        e,
                        includeIdDocuments,
                        includeEmploymentInformation,
                        idDocumentMapper,
                        employmentInformationMapper,
                        employmentInformationSalaryOverrideMapper
                ))
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE)));
    }

    @Override
    public Optional<EmployeeResponse> findByUserId(String userId, boolean includeIdDocuments, boolean includeEmploymentInformation) {
        return Optional.ofNullable(employeeRepository.findByUserId(userId)
                .map(employee -> employeeMapper.toDto(
                                employee,
                                includeIdDocuments,
                                includeEmploymentInformation,
                                idDocumentMapper,
                                employmentInformationMapper,
                                employmentInformationSalaryOverrideMapper
                        )
                ).orElseThrow(() -> new NotFoundException(userId, EMPLOYEE)));
    }

    @Override
    public List<EmployeeResponse> create(List<EmployeeRequest> employeeRequests) {

        for (EmployeeRequest request : employeeRequests) {
            employeeRepository.findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                    request.employeeNumber(),
                    request.email(),
                    request.taxPayerIdentificationNumber(),
                    request.firstName(),
                    request.lastName(),
                    request.firstName(),
                    request.middleName(),
                    request.lastName()
            ).ifPresent(employee -> {
                throw new IllegalArgumentException("Employee with employee number [%s] or email [%s] or tax payer identification number [%s] or name [%s]already exists".formatted(
                        employee.getEmployeeNumber(),
                        employee.getEmail(),
                        employee.getTaxPayerIdentificationNumber(),
                        employee.fullName()
                ));
            });
        }

        final var EMPLOYEE_TO_SAVE = employeeRequests
                .stream()
                .map(employeeRequest -> employeeMapper.toEntity(
                        employeeRequest,
                        getEmploymentInformationRequests(employeeRequest),
                        getIdDocumentRequests(employeeRequest)
                ))
                .toList();

        log.debug("Employee to save [{}]", EMPLOYEE_TO_SAVE);

        final var SAVED_EMPLOYEES = employeeRepository.saveAll(EMPLOYEE_TO_SAVE);

        return SAVED_EMPLOYEES.stream()
                .map(employee -> employeeMapper.toDto(
                        employee,
                        false,
                        false,
                        idDocumentMapper,
                        employmentInformationMapper,
                        employmentInformationSalaryOverrideMapper
                )).toList();

    }

    @Override
    public EmployeeResponse update(String id, EmployeeRequest employeeRequest) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Employee ID must be provided as path");
        }

        final var ORIGINAL_EMPLOYEE = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE));

        var EMPLOYEE_DATA = MergeUtil.merge(ORIGINAL_EMPLOYEE,
                employeeMapper.toEntity(
                        employeeRequest,
                        getEmploymentInformationRequests(employeeRequest),
                        getIdDocumentRequests(employeeRequest)
                )
        );

        final var UPDATED_EMPLOYEE = employeeRepository.save(EMPLOYEE_DATA);

        return employeeMapper.toDto(
                UPDATED_EMPLOYEE,
                false,
                false,
                idDocumentMapper,
                employmentInformationMapper,
                employmentInformationSalaryOverrideMapper
        );
    }

    @Override
    public boolean delete(String id) {
        findById(id, false, false).orElseThrow();
        employeeRepository.deleteById(id);
        return !employeeRepository.existsById(id);
    }

    private Set<IdDocument> getIdDocumentRequests(EmployeeRequest employeeRequest) {
        return employeeRequest.idDocumentRequests() != null ?
                employeeRequest.idDocumentRequests()
                        .stream()
                        .map(idDocumentMapper::toEntity)
                        .collect(Collectors.toSet()) : null;
    }

    private Set<EmploymentInformation> getEmploymentInformationRequests(EmployeeRequest employeeRequest) {
        return employeeRequest.employmentInformationRequests() != null ?
                employeeRequest.employmentInformationRequests()
                        .stream()
                        .map(e -> employmentInformationMapper.toEntity(
                                        e,
                                        employmentInformationSalaryOverrideMapper.toEntity(e.employmentInformationSalaryOverrideRequest())
                                )
                        )
                        .collect(Collectors.toSet()) : null;
    }
}
