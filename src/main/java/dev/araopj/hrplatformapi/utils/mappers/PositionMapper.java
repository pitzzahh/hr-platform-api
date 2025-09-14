package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.Position;
import lombok.experimental.UtilityClass;

/**
 * Mapper class for converting between Position entities and DTOs.
 *
 * @see Position
 */
@UtilityClass
public class PositionMapper {

    /**
     * Converts a PositionRequest DTO to a Position entity.
     *
     * @param positionRequest       the PositionRequest DTO
     * @param employmentInformation the associated EmploymentInformation entity
     * @return the corresponding Position entity
     * @throws IllegalArgumentException if positionRequest is null
     */
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

    /**
     * Converts a PositionRequest DTO to a Position entity without EmploymentInformation.
     *
     * @param positionRequest the PositionRequest DTO
     * @return the corresponding Position entity
     * @throws IllegalArgumentException if positionRequest is null
     */
    public Position toEntity(PositionRequest positionRequest) {
        if (positionRequest == null) {
            throw new IllegalArgumentException("positionRequest cannot be null");
        }

        return Position.builder()
                .code(positionRequest.code())
                .description(positionRequest.description())
                .build();
    }

    /**
     * Converts a Position entity to a PositionResponse DTO.
     *
     * @param position the Position entity
     * @return the corresponding PositionResponse DTO
     * @throws IllegalArgumentException if position is null
     */
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

    public Position toEntity(PositionResponse positionResponse) {
        if (positionResponse == null) {
            throw new IllegalArgumentException("positionResponse cannot be null");
        }

        return Position.builder()
                .code(positionResponse.code())
                .description(positionResponse.description())
                .build();
    }
}
