package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentTypeRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentTypeService;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;
import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT_TYPE;

/**
 * Service class for managing {@link IdDocumentType} entities.
 * Provides methods to create, read, update, and delete IdDocumentType records.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdDocumentTypeServiceImp implements IdDocumentTypeService {

    private final IdDocumentTypeRepository idDocumentTypeRepository;
    private final IdDocumentRepository idDocumentRepository;

    @Override
    public List<IdDocumentTypeResponse> findAll() {
        final var DATA = idDocumentTypeRepository.findAll();
        return DATA.stream()
                .map(e -> IdDocumentTypeMapper.toDto(e, true))
                .toList();
    }

    @Override
    public Optional<IdDocumentTypeResponse> findById(String id) {
        validateIdPath(id);
        return Optional.ofNullable(idDocumentTypeRepository.findById(id)
                .map(e -> IdDocumentTypeMapper.toDto(e, true))
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT_TYPE)));
    }

    @Override
    public IdDocumentTypeResponse create(IdDocumentTypeRequest idDocumentTypeRequest) {
        final var identifierId = idDocumentTypeRequest.identifierId();

        idDocumentRepository
                .findById(identifierId)
                .orElseThrow(() -> new NotFoundException(identifierId, ID_DOCUMENT));

        final var SAVED_DATA = idDocumentTypeRepository.save(
                IdDocumentTypeMapper.toEntity(idDocumentTypeRequest)
        );
        return IdDocumentTypeMapper.toDto(SAVED_DATA, false);
    }

    @Override
    public IdDocumentTypeResponse update(String id, IdDocumentTypeRequest idDocumentTypeRequest) throws InvalidRequestException {
        validateIdPath(id);

        return IdDocumentTypeMapper.toDto(idDocumentTypeRepository.save(
                IdDocumentTypeMapper.toEntity(idDocumentTypeRequest)
        ), false);
    }

    @Override
    public boolean delete(String id) {
        findById(id).orElseThrow();
        idDocumentTypeRepository.deleteById(id);
        return !idDocumentTypeRepository.existsById(id);
    }

    private static void validateIdPath(String id) {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("IdDocumentType ID must be provided as path");
        }
    }
}