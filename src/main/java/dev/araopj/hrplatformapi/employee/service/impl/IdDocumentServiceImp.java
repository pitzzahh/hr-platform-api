package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.service.IdDocumentService;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdDocumentServiceImp implements IdDocumentService {

    private final IdDocumentRepository idDocumentRepository;
    private final IdDocumentMapper idDocumentMapper;

    @Override
    public List<IdDocumentResponse> findAll() {
        var ID_DOCUMENTS = idDocumentRepository.findAll();
        return ID_DOCUMENTS.stream()
                .map(entity -> idDocumentMapper.toDto(entity, true))
                .toList();
    }

    @Override
    public Optional<IdDocumentResponse> findById(String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("id must be provided as path");
        }
        return idDocumentRepository
                .findById(id)
                .map(e -> idDocumentMapper.toDto(e, true))
                .map(Optional::of)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));
    }

    @Override
    public IdDocumentResponse create(IdDocumentRequest request) {
        idDocumentRepository.findByIdentifierNumber(request.identifierNumber())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("IdDocument with identifierNumber [%s] already exists".formatted(request.identifierNumber()));
                });
        return idDocumentMapper.toDto(idDocumentRepository.save(idDocumentMapper.toEntity(request)), false);

    }

    @Override
    public IdDocumentResponse update(String id, IdDocumentRequest request) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("id must be provided as path");
        }

        final var EXISTING_IDENTIFIER = idDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, ID_DOCUMENT));

        final var UPDATED_IDENTIFIER = idDocumentRepository.save(MergeUtil.merge(EXISTING_IDENTIFIER, idDocumentMapper.toEntity(request)));

        return idDocumentMapper.toDto(UPDATED_IDENTIFIER, false);
    }

    @Override
    public boolean delete(String id) throws BadRequestException {
        findById(id).orElseThrow();
        idDocumentRepository.deleteById(id);
        return !idDocumentRepository.existsById(id);
    }
}
