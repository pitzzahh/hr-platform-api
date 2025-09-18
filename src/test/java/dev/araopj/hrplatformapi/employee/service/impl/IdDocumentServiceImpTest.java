package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.IdDocumentRequest;
import dev.araopj.hrplatformapi.employee.dto.response.IdDocumentResponse;
import dev.araopj.hrplatformapi.employee.model.Employee;
import dev.araopj.hrplatformapi.employee.model.IdDocument;
import dev.araopj.hrplatformapi.employee.model.IdDocumentType;
import dev.araopj.hrplatformapi.employee.repository.IdDocumentRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentMapper;
import dev.araopj.hrplatformapi.utils.mappers.IdDocumentTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IdDocumentServiceImp Test")
class IdDocumentServiceImpTest {

    @Mock
    private IdDocumentRepository idDocumentRepository;
    @InjectMocks
    private IdDocumentServiceImp idDocumentServiceImp;

    private Employee employee;
    private IdDocumentType idDocumentType;
    private IdDocument idDocument;
    private IdDocumentResponse idDocumentResponse;
    private IdDocumentRequest idDocumentRequest;

    @BeforeEach
    void setup() {
        employee = Employee.builder()
                .id("emp-1")
                .build();

        idDocumentType = IdDocumentType.builder()
                .id("type-1")
                .name("Passport")
                .build();

        idDocument = IdDocument.builder()
                .id("doc-1")
                .identifierNumber("123456789")
                .idDocumentType(idDocumentType)
                .issuedDate(LocalDate.of(2023, 1, 1))
                .issuedPlace("Manila")
                .employee(employee)
                .build();

        idDocumentResponse = IdDocumentResponse.builder()
                .id("doc-1")
                .identifierNumber("123456789")
                .type(IdDocumentTypeMapper.toDto(idDocumentType, false))
                .issuedDate(LocalDate.of(2023, 1, 1))
                .issuedPlace("Manila")
                .employee(null)
                .build();

        idDocumentRequest = IdDocumentRequest.builder()
                .identifierNumber("123456789")
                .idDocumentTypeRequest(IdDocumentTypeMapper.toDto(idDocumentType))
                .issuedDate(LocalDate.of(2023, 1, 1))
                .issuedPlace("Manila")
                .employeeId("emp-1")
                .build();
    }

    @Nested
    @DisplayName("Create IdDocument Test")
    class CreateIdDocumentTest {

        @Test
        @DisplayName("Should create IdDocument successfully when valid request")
        void shouldCreateIdDocumentSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class); var typeMapperMock = mockStatic(IdDocumentTypeMapper.class)) {
                var idDocumentToSave = IdDocument.builder()
                        .identifierNumber(idDocumentRequest.identifierNumber())
                        .idDocumentType(idDocumentType)
                        .issuedDate(idDocumentRequest.issuedDate())
                        .issuedPlace(idDocumentRequest.issuedPlace())
                        .employee(employee)
                        .build();

                when(idDocumentRepository.findByIdentifierNumber(idDocumentRequest.identifierNumber()))
                        .thenReturn(Optional.empty());
                typeMapperMock.when(() -> IdDocumentTypeMapper.toEntity(idDocumentRequest.idDocumentTypeRequest()))
                        .thenReturn(idDocumentType);
                mapperMock.when(() -> IdDocumentMapper.toEntity(idDocumentRequest))
                        .thenReturn(idDocumentToSave);
                when(idDocumentRepository.save(idDocumentToSave)).thenReturn(idDocument);
                mapperMock.when(() -> IdDocumentMapper.toDto(idDocument, false)).thenReturn(idDocumentResponse);

                var result = idDocumentServiceImp.create(idDocumentRequest);

                assertNotNull(result);
                assertEquals(idDocumentResponse, result);
                assertEquals(idDocumentResponse.id(), result.id());
                assertEquals(idDocumentResponse.identifierNumber(), result.identifierNumber());
                assertEquals(idDocumentResponse.issuedDate(), result.issuedDate());
                assertEquals(idDocumentResponse.issuedPlace(), result.issuedPlace());
                assertNull(result.employee());

                verify(idDocumentRepository).findByIdentifierNumber(idDocumentRequest.identifierNumber());
                verify(idDocumentRepository).save(idDocumentToSave);
            }
        }

        @Test
        @DisplayName("Should throw exception when IdDocument already exists")
        void shouldThrowExceptionWhenIdDocumentAlreadyExists() {
            when(idDocumentRepository.findByIdentifierNumber(idDocumentRequest.identifierNumber()))
                    .thenReturn(Optional.of(idDocument));

            var exception = assertThrows(IllegalArgumentException.class,
                    () -> idDocumentServiceImp.create(idDocumentRequest));
            assertEquals("IdDocument with identifierNumber [123456789] already exists",
                    exception.getMessage());

            verify(idDocumentRepository).findByIdentifierNumber(idDocumentRequest.identifierNumber());
            verifyNoMoreInteractions(idDocumentRepository);
        }
    }

    @Nested
    @DisplayName("Find IdDocument Test")
    class FindIdDocumentTest {

        @Test
        @DisplayName("Should find all IdDocuments")
        void shouldFindAllIdDocuments() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class)) {
                var idDocuments = List.of(idDocument);
                when(idDocumentRepository.findAll()).thenReturn(idDocuments);
                mapperMock.when(() -> IdDocumentMapper.toDto(idDocument, true)).thenReturn(idDocumentResponse);

                var result = idDocumentServiceImp.findAll();

                assertNotNull(result);
                assertEquals(1, result.size());
                assertEquals(idDocumentResponse, result.getFirst());
            }
        }

        @Test
        @DisplayName("Should return empty list when no IdDocuments exist")
        void shouldReturnEmptyListWhenNoIdDocumentsExist() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class)) {
                when(idDocumentRepository.findAll()).thenReturn(List.of());

                var result = idDocumentServiceImp.findAll();

                assertNotNull(result);
                assertTrue(result.isEmpty());
                mapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should find IdDocument by id successfully when exists")
        void shouldFindIdDocumentByIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class)) {
                when(idDocumentRepository.findById(idDocument.getId()))
                        .thenReturn(Optional.of(idDocument));
                mapperMock.when(() -> IdDocumentMapper.toDto(idDocument, true))
                        .thenReturn(idDocumentResponse);

                var result = idDocumentServiceImp.findById(idDocument.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(idDocumentResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentServiceImp.findById(null));
            assertEquals("IdDocument id must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentServiceImp.findById(""));
            assertEquals("IdDocument id must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocument not found by id")
        void shouldThrowNotFoundExceptionWhenIdDocumentNotFoundById() {
            when(idDocumentRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> idDocumentServiceImp.findById("non-existent-id"));

            verify(idDocumentRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update IdDocument Test")
    class UpdateIdDocumentTest {

        @Test
        @DisplayName("Should update IdDocument successfully when valid request")
        void shouldUpdateIdDocumentSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class); var typeMapperMock = mockStatic(IdDocumentTypeMapper.class); var mergeUtilMock = mockStatic(MergeUtil.class)) {
                var updatedRequest = IdDocumentRequest.builder()
                        .identifierNumber("987654321")
                        .idDocumentTypeRequest(IdDocumentTypeMapper.toDto(idDocumentType))
                        .issuedDate(LocalDate.of(2024, 1, 1))
                        .issuedPlace("Cebu")
                        .employeeId("emp-1")
                        .build();
                var updatedIdDocument = IdDocument.builder()
                        .id("doc-1")
                        .identifierNumber(updatedRequest.identifierNumber())
                        .idDocumentType(idDocumentType)
                        .issuedDate(updatedRequest.issuedDate())
                        .issuedPlace(updatedRequest.issuedPlace())
                        .employee(employee)
                        .build();
                var updatedResponse = IdDocumentResponse.builder()
                        .id("doc-1")
                        .identifierNumber(updatedRequest.identifierNumber())
                        .type(IdDocumentTypeMapper.toDto(idDocumentType, false))
                        .issuedDate(updatedRequest.issuedDate())
                        .issuedPlace(updatedRequest.issuedPlace())
                        .employee(null)
                        .build();

                when(idDocumentRepository.findById(idDocument.getId()))
                        .thenReturn(Optional.of(idDocument));
                typeMapperMock.when(() -> IdDocumentTypeMapper.toEntity(updatedRequest.idDocumentTypeRequest()))
                        .thenReturn(idDocumentType);
                mapperMock.when(() -> IdDocumentMapper.toEntity(updatedRequest))
                        .thenReturn(updatedIdDocument);
                mergeUtilMock.when(() -> MergeUtil.merge(idDocument, updatedIdDocument))
                        .thenReturn(updatedIdDocument);
                when(idDocumentRepository.save(updatedIdDocument)).thenReturn(updatedIdDocument);
                mapperMock.when(() -> IdDocumentMapper.toDto(updatedIdDocument, false))
                        .thenReturn(updatedResponse);

                var result = idDocumentServiceImp.update(idDocument.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                assertEquals(updatedResponse.identifierNumber(), result.identifierNumber());
                assertEquals(updatedResponse.issuedDate(), result.issuedDate());
                assertEquals(updatedResponse.issuedPlace(), result.issuedPlace());
                assertNull(result.employee());

                verify(idDocumentRepository).findById(idDocument.getId());
                verify(idDocumentRepository).save(updatedIdDocument);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentServiceImp.update(null, idDocumentRequest));
            assertEquals("IdDocument id must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> idDocumentServiceImp.update("", idDocumentRequest));
            assertEquals("IdDocument id must be provided as path", exception.getMessage());

            verifyNoInteractions(idDocumentRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocument not found")
        void shouldThrowNotFoundExceptionWhenIdDocumentNotFound() {
            when(idDocumentRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> idDocumentServiceImp.update("non-existent-id", idDocumentRequest));

            verify(idDocumentRepository).findById("non-existent-id");
            verifyNoMoreInteractions(idDocumentRepository);
        }
    }

    @Nested
    @DisplayName("Delete IdDocument Test")
    class DeleteIdDocumentTest {

        @Test
        @DisplayName("Should delete IdDocument successfully when exists")
        void shouldDeleteIdDocumentSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class)) {
                when(idDocumentRepository.findById(idDocument.getId()))
                        .thenReturn(Optional.of(idDocument));
                mapperMock.when(() -> IdDocumentMapper.toDto(idDocument, true))
                        .thenReturn(idDocumentResponse);
                when(idDocumentRepository.existsById(idDocument.getId()))
                        .thenReturn(false);

                var result = idDocumentServiceImp.delete(idDocument.getId());

                assertTrue(result);
                verify(idDocumentRepository).findById(idDocument.getId());
                verify(idDocumentRepository).deleteById(idDocument.getId());
                verify(idDocumentRepository).existsById(idDocument.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when IdDocument not found")
        void shouldThrowNotFoundExceptionWhenIdDocumentNotFound() {
            when(idDocumentRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> idDocumentServiceImp.delete("non-existent-id"));

            verify(idDocumentRepository).findById("non-existent-id");
            verifyNoMoreInteractions(idDocumentRepository);
        }

        @Test
        @DisplayName("Should return false when IdDocument not deleted")
        void shouldReturnFalseWhenIdDocumentNotDeleted() {
            try (var mapperMock = mockStatic(IdDocumentMapper.class)) {
                when(idDocumentRepository.findById(idDocument.getId()))
                        .thenReturn(Optional.of(idDocument));
                mapperMock.when(() -> IdDocumentMapper.toDto(idDocument, true))
                        .thenReturn(idDocumentResponse);
                when(idDocumentRepository.existsById(idDocument.getId()))
                        .thenReturn(true);

                var result = idDocumentServiceImp.delete(idDocument.getId());

                assertFalse(result);
                verify(idDocumentRepository).findById(idDocument.getId());
                verify(idDocumentRepository).deleteById(idDocument.getId());
                verify(idDocumentRepository).existsById(idDocument.getId());
            }
        }
    }
}