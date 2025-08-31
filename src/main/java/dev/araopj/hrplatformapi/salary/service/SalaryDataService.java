package dev.araopj.hrplatformapi.salary.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.araopj.hrplatformapi.audit.dto.AuditDto;
import dev.araopj.hrplatformapi.audit.model.AuditAction;
import dev.araopj.hrplatformapi.audit.service.AuditService;
import dev.araopj.hrplatformapi.exception.SalaryGradeNotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.repository.SalaryDataRepository;
import dev.araopj.hrplatformapi.salary.repository.SalaryGradeRepository;
import dev.araopj.hrplatformapi.utils.DiffUtil;
import dev.araopj.hrplatformapi.utils.Mapper;
import dev.araopj.hrplatformapi.utils.MergeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SalaryDataService {

    private final SalaryGradeRepository salaryGradeRepository;
    private final SalaryDataRepository salaryDataRepository;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public List<SalaryData> findAll(String salaryGradeId, Limit limit) {
        var data = salaryDataRepository.findBySalaryGrade_Id(
                salaryGradeId,
                limit
        );
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", LocalDateTime.now().toString(),
                                "entity", "SalaryData",
                                "count", data.size()
                        )))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId("N/A")
                        .build()
        );
        return data;
    }

    public Optional<SalaryDataResponse> findById(String id) {
        var data = salaryDataRepository.findById(id);
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", LocalDateTime.now().toString(),
                                "entity", "SalaryData",
                                "found", data.isPresent()
                        )))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId(data.map(SalaryData::getId).orElse("N/A"))
                        .build()
        );
        return data.map(Mapper::toDto);
    }

    public Optional<SalaryDataResponse> findByIdAndSalaryGradeId(String id, String salaryGradeId) {
        var data = salaryDataRepository.findByIdAndSalaryGrade_Id(id, salaryGradeId);
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.VIEW)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", LocalDateTime.now().toString(),
                                "entity", "SalaryData",
                                "found", data.isPresent()
                        )))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId(data.map(SalaryData::getId).orElse("N/A"))
                        .build()
        );
        if (data.isEmpty()) {
            log.warn("Salary data with id {} and salary grade id {} not found", id, salaryGradeId);
            return Optional.empty();
        }
        return data.map(Mapper::toDto);
    }

    public Optional<SalaryDataResponse> create(SalaryDataRequest salaryDataRequest, String salaryGradeId) {
        var salary_grade_id = salaryDataRequest.getSalaryGradeId();

        var salaryGradeOpt = salaryGradeRepository.findById(salary_grade_id);

        if (salaryGradeOpt.isEmpty()) {
            log.warn("Checking salary grade with path variable salaryGradeId: {}", salaryGradeId);
            salaryGradeOpt = salaryGradeRepository.findById(salaryGradeId);
            if (salaryGradeOpt.isEmpty()) {
                throw new SalaryGradeNotFoundException("Salary grade with id %s not found, no salary grade to relate".formatted(salaryGradeId));
            }
        }

        var data = Mapper.toDto(salaryDataRepository.saveAndFlush(
                SalaryData.builder()
                        .step(salaryDataRequest.getStep())
                        .amount(salaryDataRequest.getAmount())
                        .salaryGrade(salaryGradeOpt.get())
                        .build()
        ));
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.CREATE)
                        .newData(objectMapper.valueToTree(data))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId(data.id())
                        .build()
        );
        return Optional.of(data);
    }

    public Optional<SalaryDataResponse> update(String id, String salaryGradeId, SalaryDataRequest salaryDataRequest) {
        var data = findByIdAndSalaryGradeId(id, salaryGradeId);
        if (data.isEmpty()) { // No existing data found to update
            log.warn("Salary data with id {} not found", id);
            return Optional.empty();
        }
        var oldData = data.get(); // Keep the old data for auditing
        var newData = MergeUtil.merge(data.get(), salaryDataRequest); // Merge old data with new request data
        var changes = DiffUtil.diff(data.get(), salaryDataRequest); // Compute the diff between old and new data

        if (changes.isEmpty()) {
            log.info("No changes detected for salary data with id {}", id); // Nothing to update
            return data;
        }

        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.UPDATE)
                        .oldData(objectMapper.valueToTree(oldData))
                        .newData(objectMapper.valueToTree(newData))
                        .changes(objectMapper.valueToTree(changes))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId(id)
                        .build()
        );

        var updatedEntity = DiffUtil.applyDiff(data.get(), changes);
        salaryDataRepository.save(Mapper.toEntity(updatedEntity));
        return Optional.of(updatedEntity);
    }

    public boolean delete(String id, String salaryGradeId) {
        var data = findByIdAndSalaryGradeId(id, salaryGradeId);
        if (data.isEmpty()) {
            log.warn("Salary data with id {} and salaryGradeId {} not found", id, salaryGradeId);
            return false;
        }
        if (!salaryDataRepository.existsById(id)) {
            log.warn("Salary data with id {} not found for deletion", id);
            return false;
        }
        salaryDataRepository.deleteById(id);
        auditService.create(
                AuditDto.builder()
                        .action(AuditAction.DELETE)
                        .newData(objectMapper.valueToTree(Map.of(
                                "timestamp", LocalDateTime.now().toString(),
                                "entity", "SalaryData",
                                "deletedId", id
                        )))
                        .performedBy("system")
                        .entityType("SalaryData")
                        .entityId(id)
                        .build()
        );
        return true;
    }
}
