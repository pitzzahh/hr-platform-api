package dev.araopj.hrplatformapi.utils.mappers;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IdDocumentMapper {

    public IdDocument toEntity(IdDocumentRequest dto) {
        if (dto == null) {
            throw new IllegalArgumentException("IdDocumentRequest cannot be null");
        }

        return IdDocument.builder()
                .identifierNumber(dto.identifierNumber())
                .idDocumentType(IdDocumentTypeMapper.toEntity(dto.idDocumentTypeRequest()))
                .issuedDate(dto.issuedDate())
                .issuedPlace(dto.issuedPlace())
                .build();
    }

    public IdDocument toEntity(IdDocumentRequest idDocumentRequest, IdDocumentType idDocumentType, Employee employee) {
        if (idDocumentRequest == null) {
            throw new IllegalArgumentException("idDocumentRequest cannot be null");
        }

        return IdDocument.builder()
                .identifierNumber(idDocumentRequest.identifierNumber())
                .idDocumentType(idDocumentType)
                .issuedDate(idDocumentRequest.issuedDate())
                .issuedPlace(idDocumentRequest.issuedPlace())
                .employee(employee)
                .build();
    }

    public IdDocumentResponse toDto(IdDocument idDocument, boolean includeEmployee) {
        if (idDocument == null) {
            throw new IllegalArgumentException("idDocument cannot be null");
        }

        return IdDocumentResponse.builder()
                .id(idDocument.getId())
                .identifierNumber(idDocument.getIdentifierNumber())
                .type(IdDocumentTypeMapper.toDto(idDocument.getIdDocumentType(), false))
                .issuedDate(idDocument.getIssuedDate())
                .issuedPlace(idDocument.getIssuedPlace())
                .employee(includeEmployee ? idDocument.getEmployee() : null)
                .createdAt(idDocument.getCreatedAt())
                .updatedAt(idDocument.getUpdatedAt())
                .build();
    }
}
