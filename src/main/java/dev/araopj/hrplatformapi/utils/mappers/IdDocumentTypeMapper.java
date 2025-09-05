package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import org.springframework.stereotype.Component;

@Component
public class IdDocumentTypeMapper {

    public IdDocumentTypeResponse toDto(IdDocumentType idDocumentType) {
        if (idDocumentType == null) {
            throw new IllegalArgumentException("idDocumentType cannot be null");
        }

        return IdDocumentTypeResponse.builder()
                .id(idDocumentType.getId())
                .code(idDocumentType.getCode())
                .name(idDocumentType.getName())
                .description(idDocumentType.getDescription())
                .createdAt(idDocumentType.getCreatedAt())
                .updatedAt(idDocumentType.getUpdatedAt())
                .build();
    }


    public IdDocumentType toEntity(IdDocumentTypeRequest idDocumentTypeRequest) {
        if (idDocumentTypeRequest == null) {
            throw new IllegalArgumentException("idDocumentType cannot be null");
        }

        return IdDocumentType.builder()
                .code(idDocumentTypeRequest.code())
                .name(idDocumentTypeRequest.name())
                .description(idDocumentTypeRequest.description())
                .build();
    }

    public IdDocumentTypeResponse toDto(IdDocument idDocument) {
        if (idDocument == null || idDocument.getIdDocumentType() == null) {
            throw new IllegalArgumentException("idDocumentType cannot be null");
        }
        return toDto(idDocument.getIdDocumentType());
    }

}
