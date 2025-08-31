package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/v1/salary-grades/{salaryGradeId}/data")
public class SalaryDataController {

    private final SalaryDataService salaryDataService;

    @GetMapping
    public ResponseEntity<StandardApiResponse<List<SalaryData>>> all(@PathVariable @NotNull String salaryGradeId, @RequestParam(defaultValue = "10") @Valid int limit) {
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.findAll(
                salaryGradeId,
                Limit.of(limit)
        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryData>> get(@PathVariable @NotNull String salaryGradeId, @PathVariable @NotNull String id) {
        return salaryDataService.findById(id)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @PostMapping
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> create(@RequestBody @Valid SalaryDataRequest salaryData, @PathVariable @NotNull String salaryGradeId) {
        log.debug("Request to create salaryData: {}", salaryData);
        return salaryDataService.create(salaryData)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with step [%s] and amount [%s] already exists".formatted(salaryData.getStep(), salaryData.getAmount()))
                                        .build()
                        ), HttpStatus.CONFLICT)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> update(@PathVariable @NotNull String salaryGradeId, @PathVariable @NotNull String id, @RequestBody @Valid SalaryDataRequest salaryDataRequest) {
        log.debug("Request to update salaryData with id {}: {}", id, salaryDataRequest);
        return salaryDataService.update(id, salaryDataRequest)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardApiResponse<Void>> delete(@PathVariable @NotNull String salaryGradeId, @PathVariable @NotNull String id) {
        log.debug("Request to delete salaryData with id {}", id);
        var isDeleted = salaryDataService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    StandardApiResponse.failure(
                            ApiError.builder()
                                    .message("SalaryData with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok().build();
    }

}
