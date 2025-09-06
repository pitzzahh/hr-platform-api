package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
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
    public Page<EmployeeResponse> findAll(Pageable pageable) {
        final var PAGINATED_DATA = employeeRepository.findAll(pageable);
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
                        e.getIdDocuments().stream()
                                .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                                .collect(Collectors.toSet()),
                        e.getEmploymentInformation().stream()
                                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                                .collect(Collectors.toSet())
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
                        e.getIdDocuments().stream()
                                .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                                .collect(Collectors.toSet()),
                        e.getEmploymentInformation().stream()
                                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                                .collect(Collectors.toSet())
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
                        employee.getIdDocuments()
                                .stream()
                                .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                                .collect(Collectors.toSet()),
                        employee.getEmploymentInformation().stream()
                                .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                                .collect(Collectors.toSet()
                                ))
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
                employeeRequest.employmentInformationRequests()
                        .stream()
                        .map(e -> employmentInformationMapper.toEntity(
                                        e,
                                        employmentInformationSalaryOverrideMapper.toEntity(e.employmentInformationSalaryOverrideRequest())
                                )
                        )
                        .collect(Collectors.toSet()),
                employeeRequest.idDocumentRequests()
                        .stream().
                        map(idDocumentMapper::toEntity)
                        .collect(Collectors.toSet())
        );

        log.debug("Employee to save [{}]", EMPLOYEE_TO_SAVE);

        auditUtil.audit(
                CREATE,
                EMPLOYEE_TO_SAVE.getId(),
                Optional.empty(),
                redact(EMPLOYEE_TO_SAVE, REDACTED),
                Optional.empty(),
                ENTITY_NAME
        );

        final var SAVED_EMPLOYEE = employeeRepository.save(EMPLOYEE_TO_SAVE);

        return employeeMapper.toDto(
                SAVED_EMPLOYEE,
                SAVED_EMPLOYEE.getIdDocuments()
                        .stream()
                        .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                        .collect(Collectors.toSet()),
                SAVED_EMPLOYEE.getEmploymentInformation()
                        .stream()
                        .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                        .collect(Collectors.toSet())
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
                        employeeRequest.employmentInformationRequests()
                                .stream()
                                .map(e -> employmentInformationMapper.toEntity(
                                                e,
                                                employmentInformationSalaryOverrideMapper.toEntity(e.employmentInformationSalaryOverrideRequest())
                                        )
                                )
                                .collect(Collectors.toSet()),
                        employeeRequest.idDocumentRequests()
                                .stream()
                                .map(idDocumentMapper::toEntity)
                                .collect(Collectors.toSet())
                )
        );

        auditUtil.audit(
                UPDATE,
                id,
                Optional.of(redact(ORIGINAL_EMPLOYEE, REDACTED)),
                redact(EMPLOYEE_DATA, REDACTED),
                Optional.of(redact(DiffUtil.diff(ORIGINAL_EMPLOYEE, EMPLOYEE_DATA), REDACTED)),
                ENTITY_NAME
        );
        final var UPDATED_EMPLOYEE = employeeRepository.save(EMPLOYEE_DATA);

        return employeeMapper.toDto(
                UPDATED_EMPLOYEE,
                UPDATED_EMPLOYEE.getIdDocuments()
                        .stream()
                        .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                        .collect(Collectors.toSet()),
                UPDATED_EMPLOYEE.getEmploymentInformation()
                        .stream()
                        .map(employmentInformation -> employmentInformationMapper.toDto(employmentInformation, false))
                        .collect(Collectors.toSet())
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
}
