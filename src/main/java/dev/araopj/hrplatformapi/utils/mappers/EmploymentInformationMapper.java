package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EmploymentInformationMapper {

    public EmploymentInformation toEntity(
            EmploymentInformationResponse employmentInformationResponse
    ) {
        if (employmentInformationResponse == null) {
            throw new IllegalArgumentException("employmentInformationRequest cannot be null");
        }

        return EmploymentInformation.builder()
                .startDate(employmentInformationResponse.startDate())
                .endDate(employmentInformationResponse.endDate())
                .employmentStatus(employmentInformationResponse.employmentStatus())
                .sourceOfFund(employmentInformationResponse.sourceOfFund())
                .remarks(employmentInformationResponse.remarks())
                .build();
    }

    public EmploymentInformation toEntity(
            EmploymentInformationRequest employmentInformationRequest
    ) {
        if (employmentInformationRequest == null) {
            throw new IllegalArgumentException("employmentInformationRequest cannot be null");
        }

        return EmploymentInformation.builder()
                .startDate(employmentInformationRequest.startDate())
                .endDate(employmentInformationRequest.endDate())
                .employmentStatus(employmentInformationRequest.employmentStatus())
                .sourceOfFund(employmentInformationRequest.sourceOfFund())
                .remarks(employmentInformationRequest.remarks())
                .build();
    }

    public EmploymentInformation toEntity(
            EmploymentInformationRequest employmentInformationRequest,
            @Nullable Salary salary
    ) {
        if (employmentInformationRequest == null) {
            throw new IllegalArgumentException("employmentInformationRequest cannot be null");
        }

        return EmploymentInformation.builder()
                .startDate(employmentInformationRequest.startDate())
                .endDate(employmentInformationRequest.endDate())
                .employmentStatus(employmentInformationRequest.employmentStatus())
                .sourceOfFund(employmentInformationRequest.sourceOfFund())
                .remarks(employmentInformationRequest.remarks())
                .salary(salary)
                .build();
    }

    public EmploymentInformation toEntity(
            EmploymentInformationRequest employmentInformationRequest,
            Employee employee,
            Salary salary,
            Position position,
            Workplace workplace
    ) {
        if (employmentInformationRequest == null) {
            throw new IllegalArgumentException("employmentInformationRequest cannot be null");
        }

        return EmploymentInformation.builder()
                .employee(employee)
                .startDate(employmentInformationRequest.startDate())
                .endDate(employmentInformationRequest.endDate())
                .employmentStatus(employmentInformationRequest.employmentStatus())
                .sourceOfFund(employmentInformationRequest.sourceOfFund())
                .remarks(employmentInformationRequest.remarks())
                .salary(salary)
                .position(position)
                .workplace(workplace)
                .build();
    }

    public EmploymentInformationResponse toDto(
            EmploymentInformation employmentInformation,
            boolean includeEmployee
    ) {
        if (employmentInformation == null) {
            throw new IllegalArgumentException("employmentInformationResponses cannot be null");
        }

        final var EMPLOYEE = employmentInformation.getEmployee();

        return EmploymentInformationResponse.builder()
                .id(employmentInformation.getId())
                .employeeResponse(includeEmployee ? EmployeeMapper.toDto(
                        EMPLOYEE,
                        true,
                        false
                ) : null)
                .startDate(employmentInformation.getStartDate())
                .endDate(employmentInformation.getEndDate())
                .employmentStatus(employmentInformation.getEmploymentStatus())
                .sourceOfFund(employmentInformation.getSourceOfFund())
                .remarks(employmentInformation.getRemarks())
                .salaryResponse(
                        employmentInformation.getSalary() != null ?
                                SalaryMapper.toDto(employmentInformation.getSalary()) : null
                )
                .build();
    }

}
