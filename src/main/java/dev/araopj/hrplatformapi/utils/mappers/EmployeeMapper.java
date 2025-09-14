package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class EmployeeMapper {

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

    public Employee toEntity(EmployeeRequest employeeRequest,
                             Set<EmploymentInformation> employmentInformation,
                             Set<IdDocument> idDocuments
    ) {
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
                .idDocuments(idDocuments)
                .employmentInformation(employmentInformation)
                .build();
    }

    public EmployeeResponse toDto(
            Employee employee,
            boolean includeIdDocuments,
            boolean includeEmploymentInformation
    ) {
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
                .idDocumentResponses(includeIdDocuments ?
                        employee.getIdDocuments()
                                .stream()
                                .map(idDocument -> IdDocumentMapper.toDto(idDocument, false))
                                .collect(java.util.stream.Collectors.toSet())
                        : null
                )
                .employmentInformationResponses(
                        includeEmploymentInformation ?
                                employee.getEmploymentInformation()
                                        .stream()
                                        .map(employmentInformation -> EmploymentInformationMapper.toDto(
                                                        employmentInformation,
                                                        false
                                                )
                                        )
                                        .collect(java.util.stream.Collectors.toSet())
                                : null
                )
                .build();
    }
}
