package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmploymentInformationMapper {

    private final EmployeeMapper employeeMapper;
    private final IdDocumentMapper idDocumentMapper;
    private final EmploymentInformationSalaryOverrideMapper employmentInformationSalaryOverrideMapper;

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
                .step(employmentInformationResponse.step())
                .anticipatedStep(employmentInformationResponse.anticipatedStep())
                .build();
    }

    public EmploymentInformation toEntity(
            EmploymentInformationRequest employmentInformationRequest,
            @Nullable EmploymentInformationSalaryOverride employmentInformationSalaryOverride
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
                .employmentInformationSalaryOverride(employmentInformationSalaryOverride)
                .step(employmentInformationRequest.step())
                .anticipatedStep(employmentInformationRequest.anticipatedStep())
                .build();
    }

    public EmploymentInformation toEntity(
            EmploymentInformationRequest employmentInformationRequest,
            Employee employee,
            @Nullable EmploymentInformationSalaryOverride employmentInformationSalaryOverride,
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
                .employmentInformationSalaryOverride(employmentInformationSalaryOverride)
                .step(employmentInformationRequest.step())
                .anticipatedStep(employmentInformationRequest.anticipatedStep())
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
                .employeeResponse(includeEmployee ? employeeMapper.toDto(
                        EMPLOYEE,
                        EMPLOYEE.getIdDocuments()
                                .stream()
                                .map(idDocument -> idDocumentMapper.toDto(idDocument, false))
                                .collect(Collectors.toSet()),
                        EMPLOYEE.getEmploymentInformation()
                                .stream()
                                .map(e -> toDto(e, false))
                                .collect(Collectors.toSet())
                ) : null)
                .startDate(employmentInformation.getStartDate())
                .endDate(employmentInformation.getEndDate())
                .employmentStatus(employmentInformation.getEmploymentStatus())
                .sourceOfFund(employmentInformation.getSourceOfFund())
                .remarks(employmentInformation.getRemarks())
                .employmentInformationSalaryOverrideResponse(
                        employmentInformation.getEmploymentInformationSalaryOverride() != null ?
                                employmentInformationSalaryOverrideMapper.toDto(employmentInformation.getEmploymentInformationSalaryOverride()) : null
                )
                .build();
    }

}
