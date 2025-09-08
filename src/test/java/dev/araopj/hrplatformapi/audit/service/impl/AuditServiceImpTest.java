package dev.araopj.hrplatformapi.audit.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.request.AuditRequest;
import dev.araopj.hrplatformapi.audit.dto.response.AuditResponse;
import dev.araopj.hrplatformapi.audit.model.Audit;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.repository.AuditRepository;
import dev.araopj.hrplatformapi.utils.mappers.AuditMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuditServiceImp Test")
class AuditServiceImpTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AuditMapper auditMapper;

    @InjectMocks
    private AuditServiceImp auditService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Audit audit;
    private AuditResponse auditResponse;
    private AuditRequest auditRequest;
    private AuditRequest.WithChanges auditRequestWithChanges;
    private AuditRequest.WithoutChanges auditRequestWithoutChanges;

    @BeforeEach
    void setUp() throws Exception {
        audit = Audit.builder()
                .id("1")
                .entityType("Employee")
                .action(AuditAction.CREATE)
                .entityId("emp1")
                .newData(objectMapper.readTree("{\"name\":\"John\"}"))
                .performedBy("user1")
                .build();

        auditResponse = AuditResponse.builder()
                .id("1")
                .entityType("Employee")
                .action(AuditAction.CREATE)
                .entityId("emp1")
                .newData(audit.getNewData())
                .performedBy("user1")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        auditRequest = AuditRequest.builder()
                .entityType("Employee")
                .action(AuditAction.CREATE)
                .entityId("emp1")
                .newData(objectMapper.readTree("{\"name\":\"John\"}"))
                .performedBy("user1")
                .build();

        auditRequestWithChanges = AuditRequest.WithChanges.builder()
                .entityType("Employee")
                .action(AuditAction.UPDATE)
                .entityId("emp1")
                .oldData(objectMapper.readTree("{\"name\":\"John\"}"))
                .newData(objectMapper.readTree("{\"name\":\"Jane\"}"))
                .changes(objectMapper.readTree("{\"name\":\"John -> Jane\"}"))
                .performedBy("user1")
                .build();

        auditRequestWithoutChanges = AuditRequest.WithoutChanges.builder()
                .entityType("Employee")
                .action(AuditAction.VIEW)
                .entityId("emp1")
                .newData(objectMapper.readTree("{\"name\":\"John\"}"))
                .performedBy("user1")
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(auditRepository, auditMapper);
    }

    @Nested
    @DisplayName("Audit Find All Tests")
    class AuditFindAllTest {

        @Test
        @DisplayName("Should return paginated audit responses")
        void shouldReturnPaginatedAuditResponses() {
            var pageable = PageRequest.of(0, 10);
            var auditPage = new PageImpl<>(List.of(audit));
            when(auditRepository.findAll(pageable)).thenReturn(auditPage);
            when(auditMapper.toDto(audit)).thenReturn(auditResponse);

            var result = auditService.findAll(pageable);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(auditResponse, result.getContent().getFirst());
            verify(auditRepository).findAll(pageable);
            verify(auditMapper).toDto(audit);
        }

        @Test
        @DisplayName("Should return empty page when no audits exist")
        void shouldReturnEmptyPageWhenNoAudits() {
            var pageable = PageRequest.of(0, 10);
            var emptyPage = new PageImpl<Audit>(List.of());
            when(auditRepository.findAll(pageable)).thenReturn(emptyPage);

            var result = auditService.findAll(pageable);

            assertNotNull(result);
            assertTrue(result.getContent().isEmpty());
            verify(auditRepository).findAll(pageable);
            verifyNoInteractions(auditMapper);
        }
    }

    @Nested
    @DisplayName("Audit Find By Id Tests")
    class AuditFindByIdTest {

        @Test
        @DisplayName("Should return audit response when ID exists")
        void shouldReturnAuditResponseWhenIdExists() {
            when(auditRepository.findById("1")).thenReturn(Optional.of(audit));
            when(auditMapper.toDto(audit)).thenReturn(auditResponse);

            var result = auditService.findById("1");

            assertTrue(result.isPresent());
            assertEquals(auditResponse, result.get());
            verify(auditRepository).findById("1");
            verify(auditMapper).toDto(audit);
        }

        @Test
        @DisplayName("Should return empty optional when ID does not exist")
        void shouldReturnEmptyOptionalWhenIdNotExists() {
            when(auditRepository.findById("1")).thenReturn(Optional.empty());

            var result = auditService.findById("1");

            assertTrue(result.isEmpty());
            verify(auditRepository).findById("1");
            verifyNoInteractions(auditMapper);
        }

        @Test
        @DisplayName("Should handle null ID")
        void shouldHandleNullId() {
            assertThrows(IllegalArgumentException.class, () -> auditService.findById(null));
            verifyNoInteractions(auditRepository);
            verifyNoInteractions(auditMapper);
        }
    }

    @Nested
    @DisplayName("Audit Create Tests")
    class AuditCreateTest {

        @Test
        @DisplayName("Should create audit with valid request")
        void shouldCreateAuditWithValidRequest() {
            when(auditMapper.toEntity(auditRequest)).thenReturn(audit);
            when(auditRepository.save(audit)).thenReturn(audit);
            when(auditMapper.toDto(audit)).thenReturn(auditResponse);

            var result = auditService.create(auditRequest);

            assertNotNull(result);
            assertEquals(auditResponse, result);
            verify(auditMapper).toEntity(auditRequest);
            verify(auditRepository).save(audit);
            verify(auditMapper).toDto(audit);
        }

        @Test
        @DisplayName("Should handle null request")
        void shouldHandleNullRequest() {
            assertThrows(IllegalArgumentException.class, () -> auditService.create(null));
            verifyNoInteractions(auditMapper);
            verifyNoInteractions(auditRepository);
        }

        @Test
        @DisplayName("Should create audit with WithChanges request")
        void shouldCreateAuditWithChangesRequest() {
            var request = AuditRequest.builder()
                    .entityType("Employee")
                    .action(AuditAction.UPDATE)
                    .entityId("emp1")
                    .oldData(auditRequestWithChanges.oldData())
                    .newData(auditRequestWithChanges.newData())
                    .changes(auditRequestWithChanges.changes())
                    .performedBy("user1")
                    .build();
            var auditWithChanges = Audit.builder()
                    .entityType("Employee")
                    .action(AuditAction.UPDATE)
                    .entityId("emp1")
                    .oldData(auditRequestWithChanges.oldData())
                    .newData(auditRequestWithChanges.newData())
                    .changes(auditRequestWithChanges.changes())
                    .performedBy("user1")
                    .build();
            var auditResponseWithChanges = AuditResponse.builder()
                    .id("1")
                    .entityType("Employee")
                    .action(AuditAction.UPDATE)
                    .entityId("emp1")
                    .oldData(auditRequestWithChanges.oldData())
                    .newData(auditRequestWithChanges.newData())
                    .changes(auditRequestWithChanges.changes())
                    .performedBy("user1")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            when(auditMapper.toEntity(request)).thenReturn(auditWithChanges);
            when(auditRepository.save(auditWithChanges)).thenReturn(auditWithChanges);
            when(auditMapper.toDto(auditWithChanges)).thenReturn(auditResponseWithChanges);

            var result = auditService.create(request);

            assertNotNull(result);
            assertEquals(auditResponseWithChanges, result);
            verify(auditMapper).toEntity(request);
            verify(auditRepository).save(auditWithChanges);
            verify(auditMapper).toDto(auditWithChanges);
        }

        @Test
        @DisplayName("Should create audit with WithoutChanges request")
        void shouldCreateAuditWithoutChangesRequest() {
            var request = AuditRequest.builder()
                    .entityType("Employee")
                    .action(AuditAction.VIEW)
                    .entityId("emp1")
                    .newData(auditRequestWithoutChanges.newData())
                    .performedBy("user1")
                    .build();
            var auditWithoutChanges = Audit.builder()
                    .entityType("Employee")
                    .action(AuditAction.VIEW)
                    .entityId("emp1")
                    .newData(auditRequestWithoutChanges.newData())
                    .performedBy("user1")
                    .build();
            var auditResponseWithoutChanges = AuditResponse.builder()
                    .id("1")
                    .entityType("Employee")
                    .action(AuditAction.VIEW)
                    .entityId("emp1")
                    .newData(auditRequestWithoutChanges.newData())
                    .performedBy("user1")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            when(auditMapper.toEntity(request)).thenReturn(auditWithoutChanges);
            when(auditRepository.save(auditWithoutChanges)).thenReturn(auditWithoutChanges);
            when(auditMapper.toDto(auditWithoutChanges)).thenReturn(auditResponseWithoutChanges);

            var result = auditService.create(request);

            assertNotNull(result);
            assertEquals(auditResponseWithoutChanges, result);
            verify(auditMapper).toEntity(request);
            verify(auditRepository).save(auditWithoutChanges);
            verify(auditMapper).toDto(auditWithoutChanges);
        }
    }
}