package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.repository.EmployeeRepository;
import dev.araopj.hrplatformapi.employee.service.EmployeeService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.AuditUtil;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.PaginationMeta;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.araopj.hrplatformapi.audit.model.AuditAction.*;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYEE;
import static dev.araopj.hrplatformapi.utils.JsonRedactor.redact;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final EmploymentInformationMapper employmentInformationMapper;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;
    private final IdDocumentMapper idDocumentMapper;
    private final AuditUtil auditUtil;
    private final Set<String> REDACTED = Set.of("employeeNumber", "itemNumber", "lastName", "email", "phoneNumber", "taxPayerIdentificationNumber", "bankAccountNumber", "userId", "idDocumentResponses");
    private final String ENTITY_NAME = EmployeeResponse.class.getName();

    @Override
    public Page<EmployeeResponse> findAll(Pageable pageable, boolean includeIdDocuments, boolean includeEmploymentInformation) {
        final var PAGINATED_DATA = includeIdDocuments && includeEmploymentInformation ?
                employeeRepository.findAllWithIdDocumentsAndEmploymentInformation(pageable) :
                includeIdDocuments ?
                        employeeRepository.findAllWithIdDocuments(pageable) :
                        includeEmploymentInformation ?
                                employeeRepository.findAllWithEmploymentInformation(pageable) :
                                employeeRepository.findAll(pageable);
        auditUtil.audit(
                VIEW,
                "[]",
                Optional.of(PaginationMeta.builder()
                        .totalElements(PAGINATED_DATA.getTotalElements())
                        .size(PAGINATED_DATA.getSize())
                        .page(PAGINATED_DATA.getNumber() + 1)
                        .totalPages(PAGINATED_DATA.getTotalPages())
                        .build()),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );

        return PAGINATED_DATA
                .map(e -> employeeMapper.toDto(
                        e,
                        getSavedIdDocuments(e),
                        getSavedEmploymentInformation(e)
                ));
    }

    @Override
    public Optional<EmployeeResponse> findById(String id) {
        auditUtil.audit(
                id,
                ENTITY_NAME
        );
        return Optional.ofNullable(employeeRepository.findById(id)
                .map(e -> employeeMapper.toDto(
                        e,
                        getSavedIdDocuments(e),
                        getSavedEmploymentInformation(e)
                ))
                .orElseThrow(() -> new NotFoundException(id, EMPLOYEE)));
    }

    @Override
    public Optional<EmployeeResponse> findByUserId(String userId) {
        auditUtil.audit(
                userId,
                ENTITY_NAME
        );
        return Optional.ofNullable(employeeRepository.findByUserId(userId)
                .map(employee -> employeeMapper.toDto(
                        employee,
                        getSavedIdDocuments(employee),
                        getSavedEmploymentInformation(employee))
                ).orElseThrow(() -> new NotFoundException(userId, EMPLOYEE)));
    }

    @Override
    public EmployeeResponse create(EmployeeRequest employeeRequest) {
        employeeRepository.findByEmployeeNumberOrEmailOrTaxPayerIdentificationNumberOrFirstNameAndLastNameOrFirstNameAndMiddleNameAndLastName(
                        employeeRequest.employeeNumber(),
                        employeeRequest.email(),
                        employeeRequest.taxPayerIdentificationNumber(),
                        employeeRequest.firstName(),
                        employeeRequest.lastName(),
                        employeeRequest.firstName(),
                        employeeRequest.middleName(),
                        employeeRequest.lastName()
                )
                .ifPresent(employee -> {
                    throw new IllegalArgumentException("Employee with employee number [%s] or email [%s] or tax payer identification number [%s] or name [%s %s] or name [%s %s %s] already exists".formatted(
                            employee.getEmployeeNumber(),
                            employee.getEmail(),
                            employee.getTaxPayerIdentificationNumber(),
                            employee.getFirstName(),
                            employee.getLastName(),
                            employee.getFirstName(),
                            employee.getMiddleName(),
                            employee.getLastName()
                    ));
                });

        final var EMPLOYEE_TO_SAVE = employeeMapper.toEntity(
                employeeRequest,
                getEmploymentInformationRequests(employeeRequest),
                getIdDocumentRequests(employeeRequest)
        );

        log.debug("Employee to save [{}]", EMPLOYEE_TO_SAVE);

        final var SAVED_EMPLOYEE = employeeRepository.save(EMPLOYEE_TO_SAVE);

        auditUtil.audit(
                CREATE,
                SAVED_EMPLOYEE.getId(),
                Optional.empty(),
                redact(SAVED_EMPLOYEE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        return employeeMapper.toDto(
                SAVED_EMPLOYEE,
                getSavedIdDocuments(SAVED_EMPLOYEE),
                getSavedEmploymentInformation(SAVED_EMPLOYEE)
        );
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

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(UPDATED_EMPLOYEE, REDACTED)),
                redact(EMPLOYEE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(UPDATED_EMPLOYEE, EMPLOYEE_DATA), REDACTED)),
                ENTITY_NAME
        );

        return employeeMapper.toDto(
                UPDATED_EMPLOYEE,
                getSavedIdDocuments(UPDATED_EMPLOYEE),
                getSavedEmploymentInformation(UPDATED_EMPLOYEE)
        );
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        auditUtil.audit(
                DELETE,
                id,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                ENTITY_NAME
        );
        employeeRepository.deleteById(id);
        return true;
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


    private Set<EmploymentInformationResponse> getSavedEmploymentInformation(Employee employee) {
        return employee.getEmploymentInformation() != null ? getEmploymentInformationResponses(employee) : null;
    }

    private Set<IdDocumentResponse> getSavedIdDocuments(Employee employee) {
        return employee.getIdDocuments() != null ? getIdDocuments(employee) : null;
    }

    private Set<EmploymentInformationResponse> getEmploymentInformationResponses(Employee employee) {
        return employee.getEmploymentInformation()
                .stream()
                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                .collect(Collectors.toSet());
    }

    private Set<IdDocumentResponse> getIdDocuments(Employee employee) {
        return employee.getIdDocuments()
                .stream()
                .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                .collect(Collectors.toSet());
    }
}
