package dev.araopj.hrplatformapi.employee.service.impl;

import dev.araopj.hrplatformapi.employee.dto.request.EmploymentInformationRequest;
import dev.araopj.hrplatformapi.employee.dto.response.EmploymentInformationResponse;
import dev.araopj.hrplatformapi.employee.model.*;
import dev.araopj.hrplatformapi.employee.repository.*;
import dev.araopj.hrplatformapi.exception.InvalidRequestException;
import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import dev.araopj.hrplatformapi.utils.formatter.DateFormatter;
import dev.araopj.hrplatformapi.utils.mappers.EmploymentInformationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static dev.araopj.hrplatformapi.exception.NotFoundException.EntityType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmploymentInformationServiceImp Test")
class EmploymentInformationServiceImpTest {

    @Mock
    private EmploymentInformationRepository employmentInformationRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private WorkplaceRepository workplaceRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @InjectMocks
    private EmploymentInformationServiceImp employmentInformationServiceImp;

    private Employee employee;
    private Position position;
    private Workplace workplace;
    private Salary salary;
    private EmploymentInformation employmentInformation;
    private EmploymentInformationResponse employmentInformationResponse;
    private EmploymentInformationRequest employmentInformationRequest;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        employee = Employee.builder()
                .id("emp-1")
                .build();

        position = Position.builder()
                .id("pos-1")
                .build();

        workplace = Workplace.builder()
                .id("work-1")
                .build();

        salary = Salary.builder()
                .id("sal-1")
                .build();

        employmentInformation = EmploymentInformation.builder()
                .id("emp-info-1")
                .employee(employee)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .employmentStatus(EmploymentStatus.PERMANENT)
                .sourceOfFund("Fund")
                .remarks("Remark")
                .position(position)
                .workplace(workplace)
                .salary(salary)
                .build();

        employmentInformationResponse = EmploymentInformationResponse.builder()
                .id("emp-info-1")
                .employeeResponse(null)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .employmentStatus(EmploymentStatus.PERMANENT)
                .sourceOfFund("Fund")
                .remarks("Remark")
                .step(1)
                .anticipatedStep(1)
                .positionResponse(null)
                .workplaceResponse(null)
                .build();

        employmentInformationRequest = EmploymentInformationRequest.builder()
                .employeeId("emp-1")
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 1, 2))
                .employmentStatus(EmploymentStatus.PERMANENT)
                .sourceOfFund("Fund")
                .remarks("Remark")
                .positionId("pos-1")
                .workplaceId("work-1")
                .salaryId("sal-1")
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("Create EmploymentInformation Test")
    class CreateEmploymentInformationTest {

        @Test
        @DisplayName("Should create EmploymentInformation successfully when valid request")
        void shouldCreateEmploymentInformationSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                        .thenReturn(Optional.of(employee));
                when(positionRepository.findById(employmentInformationRequest.positionId()))
                        .thenReturn(Optional.of(position));
                when(workplaceRepository.findById(employmentInformationRequest.workplaceId()))
                        .thenReturn(Optional.of(workplace));
                when(salaryRepository.findById(employmentInformationRequest.salaryId()))
                        .thenReturn(Optional.of(salary));
                when(employmentInformationRepository.findByStartDateAndEndDateAndRemarksAndEmployeeId(
                        eq(employmentInformationRequest.startDate()),
                        eq(employmentInformationRequest.endDate()),
                        eq(employmentInformationRequest.remarks()),
                        eq(employmentInformationRequest.employeeId())))
                        .thenReturn(Optional.empty());
                mapperMock.when(() -> EmploymentInformationMapper.toEntity(
                                eq(employmentInformationRequest), eq(employee), eq(salary), eq(position), eq(workplace)))
                        .thenReturn(employmentInformation);
                when(employmentInformationRepository.save(any(EmploymentInformation.class)))
                        .thenReturn(employmentInformation);
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);

                var result = employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest);

                assertNotNull(result);
                assertEquals(employmentInformationResponse, result);
                assertEquals(employmentInformationResponse.id(), result.id());
                assertEquals(employmentInformationResponse.startDate(), result.startDate());
                assertEquals(employmentInformationResponse.endDate(), result.endDate());
                assertEquals(employmentInformationResponse.employmentStatus(), result.employmentStatus());
                assertEquals(employmentInformationResponse.sourceOfFund(), result.sourceOfFund());
                assertEquals(employmentInformationResponse.remarks(), result.remarks());
                assertEquals(employmentInformationResponse.step(), result.step());
                assertEquals(employmentInformationResponse.anticipatedStep(), result.anticipatedStep());

                verify(employeeRepository).findById(employmentInformationRequest.employeeId());
                verify(positionRepository).findById(employmentInformationRequest.positionId());
                verify(workplaceRepository).findById(employmentInformationRequest.workplaceId());
                verify(salaryRepository).findById(employmentInformationRequest.salaryId());
                verify(employmentInformationRepository).findByStartDateAndEndDateAndRemarksAndEmployeeId(
                        employmentInformationRequest.startDate(),
                        employmentInformationRequest.endDate(),
                        employmentInformationRequest.remarks(),
                        employmentInformationRequest.employeeId());
                verify(employmentInformationRepository).save(any(EmploymentInformation.class));
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when Employee not found")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
            when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest));
            assertEquals(new NotFoundException(employmentInformationRequest.employeeId(), EMPLOYEE).getMessage(),
                    exception.getMessage());

            verify(employeeRepository).findById(employmentInformationRequest.employeeId());
            verifyNoInteractions(positionRepository, workplaceRepository, salaryRepository, employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when Position not found")
        void shouldThrowNotFoundExceptionWhenPositionNotFound() {
            when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                    .thenReturn(Optional.of(employee));
            when(positionRepository.findById(employmentInformationRequest.positionId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest));
            assertEquals(new NotFoundException(employmentInformationRequest.positionId(), POSITION).getMessage(),
                    exception.getMessage());

            verify(employeeRepository).findById(employmentInformationRequest.employeeId());
            verify(positionRepository).findById(employmentInformationRequest.positionId());
            verifyNoInteractions(workplaceRepository, salaryRepository, employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when Workplace not found")
        void shouldThrowNotFoundExceptionWhenWorkplaceNotFound() {
            when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                    .thenReturn(Optional.of(employee));
            when(positionRepository.findById(employmentInformationRequest.positionId()))
                    .thenReturn(Optional.of(position));
            when(workplaceRepository.findById(employmentInformationRequest.workplaceId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest));
            assertEquals(new NotFoundException(employmentInformationRequest.workplaceId(), WORKPLACE).getMessage(),
                    exception.getMessage());

            verify(employeeRepository).findById(employmentInformationRequest.employeeId());
            verify(positionRepository).findById(employmentInformationRequest.positionId());
            verify(workplaceRepository).findById(employmentInformationRequest.workplaceId());
            verifyNoInteractions(salaryRepository, employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when Salary not found")
        void shouldThrowNotFoundExceptionWhenSalaryNotFound() {
            when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                    .thenReturn(Optional.of(employee));
            when(positionRepository.findById(employmentInformationRequest.positionId()))
                    .thenReturn(Optional.of(position));
            when(workplaceRepository.findById(employmentInformationRequest.workplaceId()))
                    .thenReturn(Optional.of(workplace));
            when(salaryRepository.findById(employmentInformationRequest.salaryId()))
                    .thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest));
            assertEquals(new NotFoundException(employmentInformationRequest.salaryId(), SALARY).getMessage(),
                    exception.getMessage());

            verify(employeeRepository).findById(employmentInformationRequest.employeeId());
            verify(positionRepository).findById(employmentInformationRequest.positionId());
            verify(workplaceRepository).findById(employmentInformationRequest.workplaceId());
            verify(salaryRepository).findById(employmentInformationRequest.salaryId());
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when EmploymentInformation already exists")
        void shouldThrowInvalidRequestExceptionWhenEmploymentInformationAlreadyExists() {
            try (var dateFormatterMock = mockStatic(DateFormatter.class)) {
                when(employeeRepository.findById(employmentInformationRequest.employeeId()))
                        .thenReturn(Optional.of(employee));
                when(positionRepository.findById(employmentInformationRequest.positionId()))
                        .thenReturn(Optional.of(position));
                when(workplaceRepository.findById(employmentInformationRequest.workplaceId()))
                        .thenReturn(Optional.of(workplace));
                when(salaryRepository.findById(employmentInformationRequest.salaryId()))
                        .thenReturn(Optional.of(salary));
                when(employmentInformationRepository.findByStartDateAndEndDateAndRemarksAndEmployeeId(
                        eq(employmentInformationRequest.startDate()),
                        eq(employmentInformationRequest.endDate()),
                        eq(employmentInformationRequest.remarks()),
                        eq(employmentInformationRequest.employeeId())))
                        .thenReturn(Optional.of(employmentInformation));
                dateFormatterMock.when(() -> DateFormatter.format(employmentInformation.getStartDate(), "long"))
                        .thenReturn("January 01, 2020");
                dateFormatterMock.when(() -> DateFormatter.format(employmentInformation.getEndDate(), "long"))
                        .thenReturn("January 02, 2020");

                var exception = assertThrows(InvalidRequestException.class,
                        () -> employmentInformationServiceImp.create("emp-info-1", employmentInformationRequest));
                assertEquals("Workplace with start date [January 01, 2020], end date [January 02, 2020], and remarks [Remark] already exists for Employee with id [emp-1]",
                        exception.getMessage());

                verify(employeeRepository).findById(employmentInformationRequest.employeeId());
                verify(positionRepository).findById(employmentInformationRequest.positionId());
                verify(workplaceRepository).findById(employmentInformationRequest.workplaceId());
                verify(salaryRepository).findById(employmentInformationRequest.salaryId());
                verify(employmentInformationRepository).findByStartDateAndEndDateAndRemarksAndEmployeeId(
                        employmentInformationRequest.startDate(),
                        employmentInformationRequest.endDate(),
                        employmentInformationRequest.remarks(),
                        employmentInformationRequest.employeeId());
                verifyNoMoreInteractions(employmentInformationRepository);
            }
        }
    }

    @Nested
    @DisplayName("Find EmploymentInformation Test")
    class FindEmploymentInformationTest {

        @Test
        @DisplayName("Should find all EmploymentInformation")
        void shouldFindAllEmploymentInformation() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                var employmentInformationList = List.of(employmentInformation);
                var page = new PageImpl<>(employmentInformationList, pageable, employmentInformationList.size());
                when(employmentInformationRepository.findAll(pageable)).thenReturn(page);
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);

                var result = employmentInformationServiceImp.findAll(pageable);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employmentInformationResponse, result.getContent().getFirst());
            }
        }

        @Test
        @DisplayName("Should return empty page when no EmploymentInformation exists")
        void shouldReturnEmptyPageWhenNoEmploymentInformationExists() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                when(employmentInformationRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

                var result = employmentInformationServiceImp.findAll(pageable);

                assertNotNull(result);
                assertTrue(result.getContent().isEmpty());
                mapperMock.verifyNoInteractions();
            }
        }

        @Test
        @DisplayName("Should find EmploymentInformation by employee ID")
        void shouldFindEmploymentInformationByEmployeeId() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                var employmentInformationList = List.of(employmentInformation);
                var page = new PageImpl<>(employmentInformationList, pageable, employmentInformationList.size());
                when(employeeRepository.findById("emp-1")).thenReturn(Optional.of(employee));
                when(employmentInformationRepository.findByEmployeeId("emp-1", pageable)).thenReturn(page);
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);

                var result = employmentInformationServiceImp.findByEmployeeId("emp-1", pageable);

                assertNotNull(result);
                assertEquals(1, result.getContent().size());
                assertEquals(employmentInformationResponse, result.getContent().getFirst());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when employee ID is null")
        void shouldThrowInvalidRequestExceptionWhenEmployeeIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.findByEmployeeId(null, pageable));
            assertEquals("Employee ID must be provided", exception.getMessage());

            verifyNoInteractions(employeeRepository, employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when employee ID is empty")
        void shouldThrowInvalidRequestExceptionWhenEmployeeIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.findByEmployeeId("", pageable));
            assertEquals("Employee ID must be provided", exception.getMessage());

            verifyNoInteractions(employeeRepository, employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when employee not found")
        void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
            when(employeeRepository.findById("non-existent-id")).thenReturn(Optional.empty());

            var exception = assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.findByEmployeeId("non-existent-id", pageable));
            assertEquals(new NotFoundException("non-existent-id", EMPLOYEE).getMessage(),
                    exception.getMessage());

            verify(employeeRepository).findById("non-existent-id");
            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should find EmploymentInformation by ID successfully when exists")
        void shouldFindEmploymentInformationByIdSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                when(employmentInformationRepository.findById(employmentInformation.getId()))
                        .thenReturn(Optional.of(employmentInformation));
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);

                var result = employmentInformationServiceImp.findById(employmentInformation.getId());

                assertNotNull(result);
                assertTrue(result.isPresent());
                assertEquals(employmentInformationResponse, result.get());
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.findById(null));
            assertEquals("EmploymentInformation ID must be provided", exception.getMessage());

            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.findById(""));
            assertEquals("EmploymentInformation ID must be provided", exception.getMessage());

            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when EmploymentInformation not found by ID")
        void shouldThrowNotFoundExceptionWhenEmploymentInformationNotFoundById() {
            when(employmentInformationRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.findById("non-existent-id"));

            verify(employmentInformationRepository).findById("non-existent-id");
        }
    }

    @Nested
    @DisplayName("Update EmploymentInformation Test")
    class UpdateEmploymentInformationTest {

        @Test
        @DisplayName("Should update EmploymentInformation successfully when valid request")
        void shouldUpdateEmploymentInformationSuccessfullyWhenValidRequest() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class);
                 var mergeUtilMock = mockStatic(MergeUtil.class)) {
                var updatedRequest = EmploymentInformationRequest.builder()
                        .employeeId("emp-1")
                        .startDate(LocalDate.of(2021, 1, 1))
                        .endDate(LocalDate.of(2021, 1, 2))
                        .employmentStatus(EmploymentStatus.CONTRACTUAL)
                        .sourceOfFund("New Fund")
                        .remarks("New Remark")
                        .positionId("pos-1")
                        .workplaceId("work-1")
                        .salaryId("sal-1")
                        .build();
                var updatedEmploymentInformation = EmploymentInformation.builder()
                        .id("emp-info-1")
                        .employee(employee)
                        .startDate(updatedRequest.startDate())
                        .endDate(updatedRequest.endDate())
                        .employmentStatus(updatedRequest.employmentStatus())
                        .sourceOfFund(updatedRequest.sourceOfFund())
                        .remarks(updatedRequest.remarks())
                        .position(position)
                        .workplace(workplace)
                        .salary(salary)
                        .build();
                var updatedResponse = EmploymentInformationResponse.builder()
                        .id("emp-info-1")
                        .employeeResponse(null)
                        .startDate(updatedRequest.startDate())
                        .endDate(updatedRequest.endDate())
                        .employmentStatus(updatedRequest.employmentStatus())
                        .sourceOfFund(updatedRequest.sourceOfFund())
                        .remarks(updatedRequest.remarks())
                        .step(2)
                        .anticipatedStep(2)
                        .positionResponse(null)
                        .workplaceResponse(null)
                        .build();

                when(employmentInformationRepository.findById(employmentInformation.getId()))
                        .thenReturn(Optional.of(employmentInformation));
                mapperMock.when(() -> EmploymentInformationMapper.toEntity(updatedRequest))
                        .thenReturn(updatedEmploymentInformation);
                mergeUtilMock.when(() -> MergeUtil.merge(employmentInformation, updatedEmploymentInformation))
                        .thenReturn(updatedEmploymentInformation);
                when(employmentInformationRepository.save(updatedEmploymentInformation))
                        .thenReturn(updatedEmploymentInformation);
                mapperMock.when(() -> EmploymentInformationMapper.toDto(updatedEmploymentInformation, false))
                        .thenReturn(updatedResponse);

                var result = employmentInformationServiceImp.update(employmentInformation.getId(), updatedRequest);

                assertNotNull(result);
                assertEquals(updatedResponse, result);
                assertEquals(updatedResponse.startDate(), result.startDate());
                assertEquals(updatedResponse.endDate(), result.endDate());
                assertEquals(updatedResponse.employmentStatus(), result.employmentStatus());
                assertEquals(updatedResponse.sourceOfFund(), result.sourceOfFund());
                assertEquals(updatedResponse.remarks(), result.remarks());
                assertEquals(updatedResponse.step(), result.step());
                assertEquals(updatedResponse.anticipatedStep(), result.anticipatedStep());

                verify(employmentInformationRepository).findById(employmentInformation.getId());
                verify(employmentInformationRepository).save(updatedEmploymentInformation);
            }
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is null")
        void shouldThrowInvalidRequestExceptionWhenIdIsNull() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.update(null, employmentInformationRequest));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw InvalidRequestException when ID is empty")
        void shouldThrowInvalidRequestExceptionWhenIdIsEmpty() {
            var exception = assertThrows(InvalidRequestException.class,
                    () -> employmentInformationServiceImp.update("", employmentInformationRequest));
            assertEquals("Employee ID must be provided as path", exception.getMessage());

            verifyNoInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should throw NotFoundException when EmploymentInformation not found")
        void shouldThrowNotFoundExceptionWhenEmploymentInformationNotFound() {
            when(employmentInformationRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.update("non-existent-id", employmentInformationRequest));

            verify(employmentInformationRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employmentInformationRepository);
        }
    }

    @Nested
    @DisplayName("Delete EmploymentInformation Test")
    class DeleteEmploymentInformationTest {

        @Test
        @DisplayName("Should delete EmploymentInformation successfully when exists")
        void shouldDeleteEmploymentInformationSuccessfullyWhenExists() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                when(employmentInformationRepository.findById(employmentInformation.getId()))
                        .thenReturn(Optional.of(employmentInformation));
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);
                when(employmentInformationRepository.existsById(employmentInformation.getId()))
                        .thenReturn(false);

                var result = employmentInformationServiceImp.delete(employmentInformation.getId());

                assertTrue(result);
                verify(employmentInformationRepository).findById(employmentInformation.getId());
                verify(employmentInformationRepository).deleteById(employmentInformation.getId());
                verify(employmentInformationRepository).existsById(employmentInformation.getId());
            }
        }

        @Test
        @DisplayName("Should throw NotFoundException when EmploymentInformation not found")
        void shouldThrowNotFoundExceptionWhenEmploymentInformationNotFound() {
            when(employmentInformationRepository.findById("non-existent-id"))
                    .thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> employmentInformationServiceImp.delete("non-existent-id"));

            verify(employmentInformationRepository).findById("non-existent-id");
            verifyNoMoreInteractions(employmentInformationRepository);
        }

        @Test
        @DisplayName("Should return false when EmploymentInformation not deleted")
        void shouldReturnFalseWhenEmploymentInformationNotDeleted() {
            try (var mapperMock = mockStatic(EmploymentInformationMapper.class)) {
                when(employmentInformationRepository.findById(employmentInformation.getId()))
                        .thenReturn(Optional.of(employmentInformation));
                mapperMock.when(() -> EmploymentInformationMapper.toDto(employmentInformation, false))
                        .thenReturn(employmentInformationResponse);
                when(employmentInformationRepository.existsById(employmentInformation.getId()))
                        .thenReturn(true);

                var result = employmentInformationServiceImp.delete(employmentInformation.getId());

                assertFalse(result);
                verify(employmentInformationRepository).findById(employmentInformation.getId());
                verify(employmentInformationRepository).deleteById(employmentInformation.getId());
                verify(employmentInformationRepository).existsById(employmentInformation.getId());
            }
        }
    }
}