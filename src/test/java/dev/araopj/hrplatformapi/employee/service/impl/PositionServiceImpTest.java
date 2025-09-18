package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.PositionRequest;
import dev.araopj.hrplatformapi.employee.dto.response.PositionResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import dev.araopj.hrplatformapi.employee.model.Position;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.PositionRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.mappers.PositionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.EMPLOYMENT_INFORMATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PositionServiceImp Test")
class PositionServiceImpTest {

    @Mock
    private EmploymentInformationRepository employmentInformationRepository;
    @Mock
    private PositionRepository positionRepository;
    @InjectMocks
    private PositionServiceImp positionServiceImp;

    private EmploymentInformation employmentInformation;
    private Position position;
    private PositionResponse positionResponse;
    private PositionRequest positionRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .employmentStatus(EmploymentStatus.PERMANENT)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .build();

        position = Position.builder()
                .id("position-1")
                .code("ITO")
                .description("IT Officer")
                .employmentInformation(employmentInformation)
                .build();

        positionResponse = PositionResponse.builder()
                .id("position-1")
                .code("ITO")
                .description("IT Officer")
                .build();

        positionRequest = PositionRequest.builder()
                .code("ITO")
                .description("IT Officer")
                .employmentInformationId("emp-info-1")
                .build();

        pageable = Pageable.ofSize(10);
    }

    @Nested
    @DisplayName("Create Position Test")
    class CreatePositionTest {

        @Test
        @DisplayName("Should create position successfully when valid request")
        void shouldCreatePositionSuccessfullyWhenValidRequest() {
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findByCodeAndEmploymentInformationId(
                        eq(positionRequest.code()), eq(positionRequest.employmentInformationId())))
                        .thenReturn(Optional.empty());
                when(employmentInformationRepository.findById(positionRequest.employmentInformationId()))
                        .thenReturn(Optional.of(employmentInformation));
                positionMapperMock.when(() -> PositionMapper.toEntity(positionRequest, employmentInformation))
                        .thenReturn(position);
                when(positionRepository.save(position)).thenReturn(position);
                positionMapperMock.when(() -> PositionMapper.toDto(position)).thenReturn(positionResponse);

                var result = positionServiceImp.create(positionRequest);

                assertNotNull(result);
                assertEquals(positionResponse, result);
                assertEquals(positionResponse.id(), result.id());
                assertEquals(positionResponse.code(), result.code());
                assertEquals(positionResponse.description(), result.description());

                verify(positionRepository).findByCodeAndEmploymentInformationId(
                        positionRequest.code(), positionRequest.employmentInformationId());
                verify(employmentInformationRepository).findById(positionRequest.employmentInformationId());
                verify(positionRepository).save(position);
            }
        }

        @Test
        @DisplayName("Should throw exception when position already exists")
        void shouldThrowExceptionWhenPositionAlreadyExists() {
            when(positionRepository.findByCodeAndEmploymentInformationId(
                    positionRequest.code(), positionRequest.employmentInformationId()))
                    .thenReturn(Optional.of(position));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> positionServiceImp.create(positionRequest));
            assertEquals("Position with code [ITO] already exists for EmploymentInformation with id [emp-info-1]",
                    exception.getMessage());

            verify(positionRepository).findByCodeAndEmploymentInformationId(
                    positionRequest.code(), positionRequest.employmentInformationId());
            verifyNoMoreInteractions(positionRepository);
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw exception when employment information not found")
        void shouldThrowExceptionWhenEmploymentInformationNotFound() {
            when(positionRepository.findByCodeAndEmploymentInformationId(
                    positionRequest.code(), positionRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());
            when(employmentInformationRepository.findById(positionRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> positionServiceImp.create(positionRequest));
            assertEquals(new NotFoundException(positionRequest.employmentInformationId(), EMPLOYMENT_INFORMATION).getMessage(),
                    exception.getMessage());

            verify(positionRepository).findByCodeAndEmploymentInformationId(
                    positionRequest.code(), positionRequest.employmentInformationId());
            verify(employmentInformationRepository).findById(positionRequest.employmentInformationId());
            verifyNoMoreInteractions(positionRepository);
            verifyNoMoreInteractions(employmentInformationRepository);
        }
    }

    @Nested
    @DisplayName("Find Position Test")
    class FindPositionTest {

        @Test
        @DisplayName("Should find all positions paginated by 10")
        void shouldFindAllPaginatedBy10() {
            var page = new PageImpl<>(List.of(position));
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findAll(pageable))
                        .thenReturn(page);
                positionMapperMock.when(() -> PositionMapper.toDto(position))
                        .thenReturn(positionResponse);

                var result = positionServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                assertEquals(1, result.getTotalPages());
                assertFalse(result.isEmpty());
                assertEquals(positionResponse, result.getContent().getFirst());
            }
        }

        @Test
        @DisplayName("Should find position by id successfully when exists")
        void shouldFindPositionByIdSuccessfullyWhenExists() {
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findById(position.getId()))
                        .thenReturn(Optional.of(position));
                positionMapperMock.when(() -> PositionMapper.toDto(position))
                        .thenReturn(positionResponse);

                var result = positionServiceImp.findById(position.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(positionResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should return empty page when no positions exist")
        void shouldReturnEmptyPageWhenNoPositionsExist() {
            var page = new PageImpl<Position>(List.of());
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findAll(pageable))
                        .thenReturn(page);

                var result = positionServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(0, result.getTotalElements());
                assertTrue(result.isEmpty());
                assertTrue(result.getContent().isEmpty());
                positionMapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when position not found by id")
        void shouldThrowNotFoundExceptionWhenPositionNotFoundById() {
            when(positionRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> positionServiceImp.findById("non-existent-id"));

            verify(positionRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update Position Test")
    class UpdatePositionTest {

        @Test
        @DisplayName("Should update position successfully when valid request")
        void shouldUpdatePositionSuccessfullyWhenValidRequest() {
            PositionRequest updatedPositionRequest = PositionRequest.builder()
                    .code("ITO2")
                    .description("Senior IT Officer")
                    .employmentInformationId("emp-info-1")
                    .build();

            Position updatedPosition = Position.builder()
                    .id("position-1")
                    .code(updatedPositionRequest.code())
                    .description(updatedPositionRequest.description())
                    .employmentInformation(employmentInformation)
                    .build();

            PositionResponse updatedPositionResponse = PositionResponse.builder()
                    .id("position-1")
                    .code("ITO2")
                    .description("Senior IT Officer")
                    .build();

            try (var positionMapperMock = mockStatic(PositionMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                when(positionRepository.findById(position.getId()))
                        .thenReturn(Optional.of(position));
                positionMapperMock.when(() -> PositionMapper.toEntity(updatedPositionRequest))
                        .thenReturn(updatedPosition);
                mergeUtilMock.when(() -> MergeUtil.merge(position, updatedPosition))
                        .thenReturn(updatedPosition);
                when(positionRepository.save(updatedPosition)).thenReturn(updatedPosition);
                positionMapperMock.when(() -> PositionMapper.toDto(updatedPosition))
                        .thenReturn(updatedPositionResponse);

                var result = positionServiceImp.update(position.getId(), updatedPositionRequest);

                assertNotNull(result);
                assertEquals(updatedPositionResponse, result);
                assertEquals(updatedPositionResponse.code(), result.code());
                assertEquals(updatedPositionResponse.description(), result.description());

                verify(positionRepository).findById(position.getId());
                verify(positionRepository).save(updatedPosition);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            assertThrows(InvalidRequestException.class,
                    () -> positionServiceImp.update(null, positionRequest));

            verifyNoInteractions(positionRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            assertThrows(InvalidRequestException.class,
                    () -> positionServiceImp.update("", positionRequest));

            verifyNoInteractions(positionRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when position not found")
        void shouldThrowNotFoundExceptionWhenPositionNotFound() {
            when(positionRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> positionServiceImp.update("non-existent-id", positionRequest));

            verify(positionRepository).findById("non-existent-id");
            verifyNoMoreInteractions(positionRepository);
        }
    }

    @Nested
    @DisplayName("Delete Position Test")
    class DeletePositionTest {

        @Test
        @DisplayName("Should delete position successfully when exists")
        void shouldDeletePositionSuccessfullyWhenExists() {
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findById(position.getId()))
                        .thenReturn(Optional.of(position));
                positionMapperMock.when(() -> PositionMapper.toDto(position))
                        .thenReturn(positionResponse);
                when(positionRepository.existsById(position.getId()))
                        .thenReturn(false);

                boolean result = positionServiceImp.delete(position.getId());

                assertTrue(result);
                verify(positionRepository).findById(position.getId());
                verify(positionRepository).deleteById(position.getId());
                verify(positionRepository).existsById(position.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when position not found")
        void shouldThrowNotFoundExceptionWhenPositionNotFound() {
            when(positionRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> positionServiceImp.delete("non-existent-id"));

            verify(positionRepository).findById("non-existent-id");
            verifyNoMoreInteractions(positionRepository);
        }

        @Test
        @DisplayName("Should return false when position not deleted")
        void shouldReturnFalseWhenPositionNotDeleted() {
            try (var positionMapperMock = mockStatic(PositionMapper.class)) {
                when(positionRepository.findById(position.getId()))
                        .thenReturn(Optional.of(position));
                positionMapperMock.when(() -> PositionMapper.toDto(position))
                        .thenReturn(positionResponse);
                when(positionRepository.existsById(position.getId()))
                        .thenReturn(true);

                boolean result = positionServiceImp.delete(position.getId());

                assertFalse(result);
                verify(positionRepository).findById(position.getId());
                verify(positionRepository).deleteById(position.getId());
                verify(positionRepository).existsById(position.getId());
            }
        }
    }
}