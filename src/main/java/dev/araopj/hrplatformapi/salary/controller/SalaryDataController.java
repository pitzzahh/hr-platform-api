package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.ApiResponse;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/salary-data")
public class SalaryDataController {
    private final SalaryDataService salaryDataService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<SalaryData>>> all() {
        return ResponseEntity.ok(ApiResponse.success(salaryDataService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SalaryData>> get(@PathVariable @NotNull String id) {
        return salaryDataService.findById(id)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(ApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SalaryDataResponse>> create(@RequestBody @Valid SalaryData salaryData) {
        log.debug("Request to create salaryData: {}", salaryData);
        return salaryDataService.create(salaryData)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(ApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with id [%s] already exists".formatted(salaryData.getId()))
                                        .build()
                        ), HttpStatus.CONFLICT)
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SalaryDataResponse>> update(@PathVariable @NotNull String id, @RequestBody @Valid SalaryDataRequest salaryDataRequest) {
        log.debug("Request to update salaryData with id {}: {}", id, salaryDataRequest);
        return salaryDataService.update(id, salaryDataRequest)
                .map(ApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(ApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with id [%s] not found".formatted(id))
                                        .build()
                        ), HttpStatus.NOT_FOUND)
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable @NotNull String id) {
        log.debug("Request to delete salaryData with id {}", id);
        var isDeleted = salaryDataService.delete(id);
        if (!isDeleted) {
            return new ResponseEntity<>(
                    ApiResponse.failure(
                            ApiError.builder()
                                    .message("SalaryData with id [%s] not found".formatted(id))
                                    .build()
                    ), HttpStatus.NOT_FOUND
            );
        }
        return ResponseEntity.ok().build();
    }

}
