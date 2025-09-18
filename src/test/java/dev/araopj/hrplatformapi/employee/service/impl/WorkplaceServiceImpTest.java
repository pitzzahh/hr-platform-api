package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.WorkplaceRequest;
import dev.araopj.hrplatformapi.employee.dto.response.WorkplaceResponse;
import dev.araopj.hrplatformapi.employee.model.EmploymentInformation;
import dev.araopj.hrplatformapi.employee.model.EmploymentStatus;
import dev.araopj.hrplatformapi.employee.model.Workplace;
import dev.araopj.hrplatformapi.employee.repository.EmploymentInformationRepository;
import dev.araopj.hrplatformapi.employee.repository.WorkplaceRepository;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.mappers.WorkplaceMapper;
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
@DisplayName("WorkplaceServiceImp Test")
class WorkplaceServiceImpTest {

    @Mock
    private EmploymentInformationRepository employmentInformationRepository;
    @Mock
    private WorkplaceRepository workplaceRepository;
    @InjectMocks
    private WorkplaceServiceImp workplaceServiceImp;

    private EmploymentInformation employmentInformation;
    private Workplace workplace;
    private WorkplaceResponse workplaceResponse;
    private WorkplaceRequest workplaceRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .employmentStatus(EmploymentStatus.PERMANENT)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .build();

        workplace = Workplace.builder()
                .id("workplace-1")
                .code("HO")
                .name("Head Office")
                .shortName("Head Office")
                .employmentInformation(employmentInformation)
                .build();

        workplaceResponse = WorkplaceResponse.builder()
                .id("workplace-1")
                .code("HO")
                .name("Head Office")
                .shortName("Head Office")
                .build();

        workplaceRequest = WorkplaceRequest.builder()
                .code("HO")
                .name("Head Office")
                .shortName("Head Office")
                .employmentInformationId("emp-info-1")
                .build();

        pageable = Pageable.ofSize(10);
    }

    @Nested
    @DisplayName("Create Workplace Test")
    class CreateWorkplaceTest {

        @Test
        @DisplayName("Should create workplace successfully when valid request")
        void shouldCreateWorkplaceSuccessfullyWhenValidRequest() {
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findByCodeAndNameAndEmploymentInformationId(
                        eq(workplaceRequest.code()), eq(workplaceRequest.name()), eq(workplaceRequest.employmentInformationId())))
                        .thenReturn(Optional.empty());
                when(employmentInformationRepository.findById(workplaceRequest.employmentInformationId()))
                        .thenReturn(Optional.of(employmentInformation));
                workplaceMapperMock.when(() -> WorkplaceMapper.toEntity(workplaceRequest, employmentInformation))
                        .thenReturn(workplace);
                when(workplaceRepository.save(workplace)).thenReturn(workplace);
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(workplace, false)).thenReturn(workplaceResponse);

                var result = workplaceServiceImp.create(workplaceRequest);

                assertNotNull(result);
                assertEquals(workplaceResponse, result);
                assertEquals(workplaceResponse.id(), result.id());
                assertEquals(workplaceResponse.code(), result.code());
                assertEquals(workplaceResponse.name(), result.name());
                assertEquals(workplaceResponse.shortName(), result.shortName());

                verify(workplaceRepository).findByCodeAndNameAndEmploymentInformationId(
                        workplaceRequest.code(), workplaceRequest.name(), workplaceRequest.employmentInformationId());
                verify(employmentInformationRepository).findById(workplaceRequest.employmentInformationId());
                verify(workplaceRepository).save(workplace);
            }
        }

        @Test
        @DisplayName("Should throw exception when workplace already exists")
        void shouldThrowExceptionWhenWorkplaceAlreadyExists() {
            when(workplaceRepository.findByCodeAndNameAndEmploymentInformationId(
                    workplaceRequest.code(), workplaceRequest.name(), workplaceRequest.employmentInformationId()))
                    .thenReturn(Optional.of(workplace));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> workplaceServiceImp.create(workplaceRequest));
            assertEquals("Workplace with code [HO] and name [Head Office] already exists for EmploymentInformation with id [emp-info-1]",
                    exception.getMessage());

            verify(workplaceRepository).findByCodeAndNameAndEmploymentInformationId(
                    workplaceRequest.code(), workplaceRequest.name(), workplaceRequest.employmentInformationId());
            verifyNoMoreInteractions(workplaceRepository);
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw exception when employment information not found")
        void shouldThrowExceptionWhenEmploymentInformationNotFound() {
            when(workplaceRepository.findByCodeAndNameAndEmploymentInformationId(
                    workplaceRequest.code(), workplaceRequest.name(), workplaceRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());
            when(employmentInformationRepository.findById(workplaceRequest.employmentInformationId()))
                    .thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> workplaceServiceImp.create(workplaceRequest));
            assertEquals(new NotFoundException(workplaceRequest.employmentInformationId(), EMPLOYMENT_INFORMATION).getMessage(),
                    exception.getMessage());

            verify(workplaceRepository).findByCodeAndNameAndEmploymentInformationId(
                    workplaceRequest.code(), workplaceRequest.name(), workplaceRequest.employmentInformationId());
            verify(employmentInformationRepository).findById(workplaceRequest.employmentInformationId());
            verifyNoMoreInteractions(workplaceRepository);
            verifyNoMoreInteractions(employmentInformationRepository);
        }
    }

    @Nested
    @DisplayName("Find Workplace Test")
    class FindWorkplaceTest {

        @Test
        @DisplayName("Should find all workplaces paginated by 10")
        void shouldFindAllPaginatedBy10() {
            var page = new PageImpl<>(List.of(workplace));
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findAll(pageable))
                        .thenReturn(page);
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(workplace, false))
                        .thenReturn(workplaceResponse);

                var result = workplaceServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                assertEquals(1, result.getTotalPages());
                assertFalse(result.isEmpty());
                assertEquals(workplaceResponse, result.getContent().getFirst());
            }
        }

        @Test
        @DisplayName("Should find workplace by id successfully when exists")
        void shouldFindWorkplaceByIdSuccessfullyWhenExists() {
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findById(workplace.getId()))
                        .thenReturn(Optional.of(workplace));
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(workplace, false))
                        .thenReturn(workplaceResponse);

                var result = workplaceServiceImp.findById(workplace.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(workplaceResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should return empty page when no workplaces exist")
        void shouldReturnEmptyPageWhenNoWorkplacesExist() {
            var page = new PageImpl<Workplace>(List.of());
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findAll(pageable))
                        .thenReturn(page);

                var result = workplaceServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(0, result.getTotalElements());
                assertTrue(result.isEmpty());
                assertTrue(result.getContent().isEmpty());
                workplaceMapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when workplace not found by id")
        void shouldThrowNotFoundExceptionWhenWorkplaceNotFoundById() {
            when(workplaceRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> workplaceServiceImp.findById("non-existent-id"));

            verify(workplaceRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update Workplace Test")
    class UpdateWorkplaceTest {

        @Test
        @DisplayName("Should update workplace successfully when valid request")
        void shouldUpdateWorkplaceSuccessfullyWhenValidRequest() {
            WorkplaceRequest updatedWorkplaceRequest = WorkplaceRequest.builder()
                    .code("HO2")
                    .name("Main Office")
                    .shortName("Main Office")
                    .employmentInformationId("emp-info-1")
                    .build();

            Workplace updatedWorkplace = Workplace.builder()
                    .id("workplace-1")
                    .code(updatedWorkplaceRequest.code())
                    .name(updatedWorkplaceRequest.name())
                    .shortName(updatedWorkplaceRequest.shortName())
                    .employmentInformation(employmentInformation)
                    .build();

            WorkplaceResponse updatedWorkplaceResponse = WorkplaceResponse.builder()
                    .id("workplace-1")
                    .code(updatedWorkplaceRequest.code())
                    .name(updatedWorkplaceRequest.name())
                    .shortName(updatedWorkplaceRequest.shortName())
                    .build();

            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                workplaceMapperMock.when(() -> WorkplaceMapper.toEntity(updatedWorkplaceRequest))
                        .thenReturn(updatedWorkplace);
                when(workplaceRepository.save(updatedWorkplace)).thenReturn(updatedWorkplace);
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(updatedWorkplace, false))
                        .thenReturn(updatedWorkplaceResponse);

                var result = workplaceServiceImp.update(workplace.getId(), updatedWorkplaceRequest);

                assertNotNull(result);
                assertEquals(updatedWorkplaceResponse, result);
                assertEquals(updatedWorkplaceResponse.code(), result.code());
                assertEquals(updatedWorkplaceResponse.name(), result.name());
                assertEquals(updatedWorkplaceResponse.shortName(), result.shortName());

                verify(workplaceRepository).save(updatedWorkplace);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            assertThrows(InvalidRequestException.class,
                    () -> workplaceServiceImp.update(null, workplaceRequest));

            verifyNoInteractions(workplaceRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when id is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            assertThrows(InvalidRequestException.class,
                    () -> workplaceServiceImp.update("", workplaceRequest));

            verifyNoInteractions(workplaceRepository);
        }
    }

    @Nested
    @DisplayName("Delete Workplace Test")
    class DeleteWorkplaceTest {

        @Test
        @DisplayName("Should delete workplace successfully when exists")
        void shouldDeleteWorkplaceSuccessfullyWhenExists() {
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findById(workplace.getId()))
                        .thenReturn(Optional.of(workplace));
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(workplace, false))
                        .thenReturn(workplaceResponse);
                when(workplaceRepository.existsById(workplace.getId()))
                        .thenReturn(false);

                boolean result = workplaceServiceImp.delete(workplace.getId());

                assertTrue(result);
                verify(workplaceRepository).findById(workplace.getId());
                verify(workplaceRepository).deleteById(workplace.getId());
                verify(workplaceRepository).existsById(workplace.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when workplace not found")
        void shouldThrowNotFoundExceptionWhenWorkplaceNotFound() {
            when(workplaceRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> workplaceServiceImp.delete("non-existent-id"));

            verify(workplaceRepository).findById("non-existent-id");
            verifyNoMoreInteractions(workplaceRepository);
        }

        @Test
        @DisplayName("Should return false when workplace not deleted")
        void shouldReturnFalseWhenWorkplaceNotDeleted() {
            try (var workplaceMapperMock = mockStatic(WorkplaceMapper.class)) {
                when(workplaceRepository.findById(workplace.getId()))
                        .thenReturn(Optional.of(workplace));
                workplaceMapperMock.when(() -> WorkplaceMapper.toDto(workplace, false))
                        .thenReturn(workplaceResponse);
                when(workplaceRepository.existsById(workplace.getId()))
                        .thenReturn(true);

                boolean result = workplaceServiceImp.delete(workplace.getId());

                assertFalse(result);
                verify(workplaceRepository).findById(workplace.getId());
                verify(workplaceRepository).deleteById(workplace.getId());
                verify(workplaceRepository).existsById(workplace.getId());
            }
        }
    }
}