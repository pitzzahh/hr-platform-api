package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmployeeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmployeeResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final IdDocumentMapper idDocumentMapper;

    public Employee toEntity(EmployeeRequest employeeRequest, final Set<IdDocument> idDocuments) {
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
                        .stream().map(idDocumentMapper::toEntity)
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
                        .stream().map(idDocumentMapper::toDto)
                        .collect(Collectors.toSet())
                )
                .build();
    }
}
