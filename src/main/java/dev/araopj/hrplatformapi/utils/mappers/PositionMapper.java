package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.Position;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Position entities and DTOs.
 */
@Component
public class PositionMapper {

    public Position toEntity(PositionRequest positionRequest, EmploymentInformation employmentInformation) {
        if (positionRequest == null) {
            throw new IllegalArgumentException("positionRequest cannot be null");
        }

        return Position.builder()
                .code(positionRequest.code())
                .description(positionRequest.description())
                .employmentInformation(employmentInformation)
                .build();
    }

    public PositionResponse toDto(Position position) {
        if (position == null) {
            throw new IllegalArgumentException("position cannot be null");
        }

        return PositionResponse.builder()
                .id(position.getId())
                .code(position.getCode())
                .description(position.getDescription())
                .createdAt(position.getCreatedAt())
                .updatedAt(position.getUpdatedAt())
                .build();
    }
}
