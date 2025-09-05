package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final IdDocumentMapper idDocumentMapper;
    private final EmploymentInformationMapper employmentInformationMapper;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;

    public Employee toEntity(EmployeeResponse employeeResponse) {
        if (employeeResponse == null) {
            throw new IllegalArgumentException("employeeResponse cannot be null");
        }

        return Employee.builder()
                .employeeNumber(employeeResponse.employeeNumber())
                .itemNumber(employeeResponse.itemNumber())
                .firstName(employeeResponse.firstName())
                .middleName(employeeResponse.middleName())
                .lastName(employeeResponse.lastName())
                .photo(employeeResponse.photo())
                .dateOfBirth(employeeResponse.dateOfBirth())
                .email(employeeResponse.email())
                .phoneNumber(employeeResponse.phoneNumber())
                .gender(employeeResponse.gender())
                .taxPayerIdentificationNumber(employeeResponse.taxPayerIdentificationNumber())
                .civilStatus(employeeResponse.civilStatus())
                .bankAccountNumber(employeeResponse.bankAccountNumber())
                .archived(employeeResponse.archived())
                .userId(employeeResponse.userId())
                .build();
    }

    public Employee toEntity(EmployeeRequest employeeRequest) {
        if (employeeRequest == null) {
            throw new IllegalArgumentException("employeeRequest cannot be null");
        }

        return Employee.builder()
                .employeeNumber(employeeRequest.employeeNumber())
                .itemNumber(employeeRequest.itemNumber())
                .firstName(employeeRequest.firstName())
                .middleName(employeeRequest.middleName())
                .lastName(employeeRequest.lastName())
                .photo(employeeRequest.photo())
                .dateOfBirth(employeeRequest.dateOfBirth())
                .email(employeeRequest.email())
                .phoneNumber(employeeRequest.phoneNumber())
                .gender(employeeRequest.gender())
                .taxPayerIdentificationNumber(employeeRequest.taxPayerIdentificationNumber())
                .civilStatus(employeeRequest.civilStatus())
                .bankAccountNumber(employeeRequest.bankAccountNumber())
                .archived(employeeRequest.archived())
                .userId(employeeRequest.userId())
                .idDocuments(employeeRequest.idDocumentRequests()
                        .stream().
                        map(idDocumentMapper::toEntity)
                        .collect(Collectors.toSet())
                )
                .employmentInformation(
                        employeeRequest.employmentInformationRequests()
                                .stream()
                                .map(e -> employmentInformationMapper.toEntity(
                                                e,
                                                employmentInformationSalaryOverrideMapper.toEntity(e.employmentInformationSalaryOverrideRequest())
                                        )
                                )
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public EmployeeResponse toDto(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("employee cannot be null");
        }

        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeNumber(employee.getEmployeeNumber())
                .itemNumber(employee.getItemNumber())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastName(employee.getLastName())
                .photo(employee.getPhoto())
                .dateOfBirth(employee.getDateOfBirth())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .gender(employee.getGender())
                .taxPayerIdentificationNumber(employee.getTaxPayerIdentificationNumber())
                .civilStatus(employee.getCivilStatus())
                .bankAccountNumber(employee.getBankAccountNumber())
                .archived(employee.isArchived())
                .userId(employee.getUserId())
                .identifiers(employee.getIdDocuments()
                        .stream()
                        .map(e -> idDocumentMapper.toDto(e, false))
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
