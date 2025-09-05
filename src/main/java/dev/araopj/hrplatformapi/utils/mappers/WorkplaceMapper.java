package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.Workplace;
import org.springframework.stereotype.Component;

@Component
public class WorkplaceMapper {
    public Workplace toEntity(WorkplaceRequest workplaceRequest, EmploymentInformation employmentInformation) {
        if (workplaceRequest == null) {
            throw new IllegalArgumentException("workplaceRequest cannot be null");
        }

        return Workplace.builder()
                .code(workplaceRequest.code())
                .name(workplaceRequest.name())
                .shortName(workplaceRequest.shortName())
                .employmentInformation(employmentInformation)
                .build();
    }

    public Workplace toEntity(WorkplaceRequest.WithoutEmploymentInformationId workplaceRequest) {
        if (workplaceRequest == null) {
            throw new IllegalArgumentException("workplaceRequest cannot be null");
        }

        return Workplace.builder()
                .code(workplaceRequest.code())
                .name(workplaceRequest.name())
                .shortName(workplaceRequest.shortName())
                .build();
    }

    public WorkplaceResponse toDto(Workplace workplace, boolean includeEmploymentInformation) {
        if (workplace == null) {
            throw new IllegalArgumentException("workplace cannot be null");
        }

        return WorkplaceResponse.builder()
                .id(workplace.getId())
                .code(workplace.getCode())
                .name(workplace.getName())
                .shortName(workplace.getShortName())
                .employmentInformation(includeEmploymentInformation ? workplace.getEmploymentInformation() : null)
                .createdAt(workplace.getCreatedAt())
                .updatedAt(workplace.getUpdatedAt())
                .build();
    }

    public Workplace toEntity(WorkplaceResponse workplaceResponse) {
        if (workplaceResponse == null) {
            throw new IllegalArgumentException("workplaceResponse cannot be null");
        }

        return Workplace.builder()
                .code(workplaceResponse.code())
                .name(workplaceResponse.name())
                .shortName(workplaceResponse.shortName())
                .build();
    }
}
