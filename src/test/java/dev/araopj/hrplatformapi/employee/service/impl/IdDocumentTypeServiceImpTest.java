package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentTypeRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentTypeResponse;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentTypeRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.ID_DOCUMENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdDocumentTypeServiceImp Test")
class IdDocumentTypeServiceImpTest {

    @Mock
    private IdDocumentTypeRepository idDocumentTypeRepository;
    @Mock
    private IdDocumentRepository idDocumentRepository;
    @InjectMocks
    private IdDocumentTypeServiceImp idDocumentTypeServiceImp;

    private IdDocument idDocument;
    private IdDocumentType idDocumentType;
    private IdDocumentTypeResponse idDocumentTypeResponse;
    private IdDocumentTypeRequest idDocumentTypeRequest;

    @BeforeEach
    void setup() {
        idDocument = IdDocument.builder()
                .id("doc-1")
                .build();

        idDocumentType = IdDocumentType.builder()
                .id("type-1")
                .code("PASS")
                .name("Passport")
                .description("International travel document")
                .idDocument(idDocument)
                .build();

        idDocumentTypeResponse = IdDocumentTypeResponse.builder()
                .id("type-1")
                .code("PASS")
                .name("Passport")
                .description("International travel document")
                .idDocument(null)
                .build();

        idDocumentTypeRequest = IdDocumentTypeRequest.builder()
                .identifierId("doc-1")
                .code("PASS")
                .name("Passport")
                .description("International travel document")
                .build();
    }

    @Nested
    @DisplayName("Create IdDocumentType Test")
    class CreateIdDocumentTypeTest {

        @Test
        @DisplayName("Should create IdDocumentType successfully when valid request")
        void shouldCreateIdDocumentTypeSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                when(idDocumentRepository.findById(idDocumentTypeRequest.identifierId()))
                        .thenReturn(Optional.of(idDocument));
                mapperMock.when(() -> IdDocumentTypeMapper.toEntity(idDocumentTypeRequest))
                        .thenReturn(idDocumentType);
                when(idDocumentTypeRepository.save(any(IdDocumentType.class)))
                        .thenReturn(idDocumentType);
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(idDocumentType, false))
                        .thenReturn(idDocumentTypeResponse);

                var result = idDocumentTypeServiceImp.create(idDocumentTypeRequest);

                assertNotNull(result);
                assertEquals(idDocumentTypeResponse, result);
                assertEquals(idDocumentTypeResponse.id(), result.id());
                assertEquals(idDocumentTypeResponse.code(), result.code());
                assertEquals(idDocumentTypeResponse.name(), result.name());
                assertEquals(idDocumentTypeResponse.description(), result.description());
                assertNull(result.idDocument());

                verify(idDocumentRepository).findById(idDocumentTypeRequest.identifierId());
                verify(idDocumentTypeRepository).save(any(IdDocumentType.class));
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocument not found")
        void shouldThrowNotFoundExceptionWhenIdDocumentNotFound() {
            when(idDocumentRepository.findById(idDocumentTypeRequest.identifierId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> idDocumentTypeServiceImp.create(idDocumentTypeRequest));
            assertEquals(new NotFoundException(idDocumentTypeRequest.identifierId(), ID_DOCUMENT).getMessage(),
                    exception.getMessage());

            verify(idDocumentRepository).findById(idDocumentTypeRequest.identifierId());
            verifyNoInteractions(idDocumentTypeRepository);
        }
    }

    @Nested
    @DisplayName("Find IdDocumentType Test")
    class FindIdDocumentTypeTest {

        @Test
        @DisplayName("Should find all IdDocumentTypes")
        void shouldFindAllIdDocumentTypes() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                var idDocumentTypes = List.of(idDocumentType);
                when(idDocumentTypeRepository.findAll()).thenReturn(idDocumentTypes);
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(idDocumentType, true))
                        .thenReturn(idDocumentTypeResponse);

                var result = idDocumentTypeServiceImp.findAll();

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(idDocumentTypeResponse, result.getFirst());
                assertEquals(
                        idDocumentTypes.stream()
                                .map(e -> IdDocumentTypeMapper.toDto(e, true))
                                .toList(),
                        result
                );
            }
        }

        @Test
        @DisplayName("Should return empty list when no IdDocumentTypes exist")
        void shouldReturnEmptyListWhenNoIdDocumentTypesExist() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                when(idDocumentTypeRepository.findAll()).thenReturn(List.of());

                var result = idDocumentTypeServiceImp.findAll();

                assertNotNull(result);
                assertTrue(result.isEmpty());
                mapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should find IdDocumentType by id successfully when exists")
        void shouldFindIdDocumentTypeByIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                when(idDocumentTypeRepository.findById(idDocumentType.getId()))
                        .thenReturn(Optional.of(idDocumentType));
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(idDocumentType, true))
                        .thenReturn(idDocumentTypeResponse);

                var result = idDocumentTypeServiceImp.findById(idDocumentType.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(idDocumentTypeResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocumentType not found by id")
        void shouldThrowNotFoundExceptionWhenIdDocumentTypeNotFoundById() {
            when(idDocumentTypeRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> idDocumentTypeServiceImp.findById("non-existent-id"));

            verify(idDocumentTypeRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update IdDocumentType Test")
    class UpdateIdDocumentTypeTest {

        @Test
        @DisplayName("Should update IdDocumentType successfully when valid request")
        void shouldUpdateIdDocumentTypeSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                var updatedRequest = IdDocumentTypeRequest.builder()
                        .identifierId("doc-1")
                        .code("VISA")
                        .name("Visa")
                        .description("Travel visa document")
                        .build();
                var updatedIdDocumentType = IdDocumentType.builder()
                        .id("type-1")
                        .code(updatedRequest.code())
                        .name(updatedRequest.name())
                        .description(updatedRequest.description())
                        .idDocument(idDocument)
                        .build();
                var updatedResponse = IdDocumentTypeResponse.builder()
                        .id("type-1")
                        .code(updatedRequest.code())
                        .name(updatedRequest.name())
                        .description(updatedRequest.description())
                        .idDocument(null)
                        .build();

                mapperMock.when(() -> IdDocumentTypeMapper.toEntity(updatedRequest))
                        .thenReturn(updatedIdDocumentType);
                when(idDocumentTypeRepository.save(updatedIdDocumentType)).thenReturn(updatedIdDocumentType);
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(updatedIdDocumentType, false))
                        .thenReturn(updatedResponse);

                var result = idDocumentTypeServiceImp.update(idDocumentType.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                assertEquals(updatedResponse.code(), result.code());
                assertEquals(updatedResponse.name(), result.name());
                assertEquals(updatedResponse.description(), result.description());
                assertNull(result.idDocument());

                verify(idDocumentTypeRepository).save(updatedIdDocumentType);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentTypeServiceImp.update(null, idDocumentTypeRequest));
            assertEquals("IdDocumentType ID must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentTypeRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentTypeServiceImp.update("", idDocumentTypeRequest));
            assertEquals("IdDocumentType ID must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentTypeRepository);
        }
    }

    @Nested
    @DisplayName("Delete IdDocumentType Test")
    class DeleteIdDocumentTypeTest {

        @Test
        @DisplayName("Should delete IdDocumentType successfully when exists")
        void shouldDeleteIdDocumentTypeSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                when(idDocumentTypeRepository.findById(idDocumentType.getId()))
                        .thenReturn(Optional.of(idDocumentType));
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(idDocumentType, true))
                        .thenReturn(idDocumentTypeResponse);
                when(idDocumentTypeRepository.existsById(idDocumentType.getId()))
                        .thenReturn(false);

                var result = idDocumentTypeServiceImp.delete(idDocumentType.getId());

                assertTrue(result);
                verify(idDocumentTypeRepository).findById(idDocumentType.getId());
                verify(idDocumentTypeRepository).deleteById(idDocumentType.getId());
                verify(idDocumentTypeRepository).existsById(idDocumentType.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocumentType not found")
        void shouldThrowNotFoundExceptionWhenIdDocumentTypeNotFound() {
            when(idDocumentTypeRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> idDocumentTypeServiceImp.delete("non-existent-id"));

            verify(idDocumentTypeRepository).findById("non-existent-id");
            verifyNoMoreInteractions(idDocumentTypeRepository);
        }

        @Test
        @DisplayName("Should return false when IdDocumentType not deleted")
        void shouldReturnFalseWhenIdDocumentTypeNotDeleted() {
            try (var mapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                when(idDocumentTypeRepository.findById(idDocumentType.getId()))
                        .thenReturn(Optional.of(idDocumentType));
                mapperMock.when(() -> IdDocumentTypeMapper.toDto(idDocumentType, true))
                        .thenReturn(idDocumentTypeResponse);
                when(idDocumentTypeRepository.existsById(idDocumentType.getId()))
                        .thenReturn(true);

                var result = idDocumentTypeServiceImp.delete(idDocumentType.getId());

                assertFalse(result);
                verify(idDocumentTypeRepository).findById(idDocumentType.getId());
                verify(idDocumentTypeRepository).deleteById(idDocumentType.getId());
                verify(idDocumentTypeRepository).existsById(idDocumentType.getId());
            }
        }
    }
}