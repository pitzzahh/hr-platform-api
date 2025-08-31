package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.model.SalaryData;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Salary Data",
        description = "Endpoints for managing salary data associated with specific salary grades."
)
public class SalaryDataController {

    private final SalaryDataService salaryDataService;

    @GetMapping
    @Operation(
            description = "Retrieve a list of all salary data entries for a specific salary grade.",
            summary = "Get all salary data for a salary grade",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of salary data"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters provided"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<List<SalaryData>>> all(@PathVariable @NotNull String salaryGradeId, @RequestParam(defaultValue = "10") @Valid int limit) {
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.findAll(
                salaryGradeId,
                Limit.of(limit)
        )));
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Retrieve a specific salary data entry by its ID, with an optional check for association with a specific salary grade.",
            summary = "Get salary data by ID with optional salary grade check",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary data"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid parameters provided"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> get(
            @PathVariable String id,
            @PathVariable(required = false) String salaryGradeId,
            @RequestParam(defaultValue = "false") boolean checkSalaryGrade
    ) {
        var response = checkSalaryGrade
                ? salaryDataService.findByIdAndSalaryGradeId(id, salaryGradeId)
                : salaryDataService.findById(id);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardApiResponse.failure(
                        ApiError.builder()
                                .message(checkSalaryGrade && salaryGradeId != null
                                        ? "SalaryData with id [%s] not found in SalaryGrade with id [%s]".formatted(id, salaryGradeId)
                                        : "SalaryData with id [%s] not found".formatted(id))
                                .build()
                )));
    }

    @PostMapping
    @Operation(
            description = "Create a new salary data entry for a specific salary grade.",
            summary = "Create salary data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary data"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary data provided"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Salary data with the same step and amount already exists"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> create(
            @RequestBody @Valid SalaryDataRequest salaryDataRequest,
            @PathVariable @NotNull String salaryGradeId,
            @RequestParam(defaultValue = "false") boolean usePathSalaryGradeId
    ) {
        log.debug("Request to create salaryDataRequest: {}", salaryDataRequest);
        return salaryDataService.create(salaryDataRequest, usePathSalaryGradeId ? salaryGradeId : salaryDataRequest.getSalaryGradeId())
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(StandardApiResponse.failure(
                                ApiError.builder()
                                        .message("SalaryData with step [%s] and amount [%s] already exists".formatted(salaryDataRequest.getStep(), salaryDataRequest.getAmount()))
                                        .build()
                        ), HttpStatus.CONFLICT)
                );
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Update an existing salary data entry by its ID.",
            summary = "Update salary data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated the salary data"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary data provided"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> update(
            @PathVariable @NotNull String salaryGradeId,
            @PathVariable @NotNull String id,
            @RequestBody @Valid SalaryDataRequest salaryDataRequest,
            @RequestParam(defaultValue = "false") boolean usePathSalaryGradeId
    ) {
        log.info("Request to update salary data with id {}: {}", id, salaryGradeId);
        var effectiveSalaryGradeId = usePathSalaryGradeId ? salaryGradeId : salaryDataRequest.getSalaryGradeId();
        return salaryDataService.update(id, effectiveSalaryGradeId, salaryDataRequest,
                        !usePathSalaryGradeId && salaryDataRequest.getSalaryGradeId() != null, usePathSalaryGradeId)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(StandardApiResponse.failure(
                        ApiError.builder()
                                .message(effectiveSalaryGradeId != null
                                        ? "SalaryData with id [%s] not found in SalaryGrade with id [%s]".formatted(id, effectiveSalaryGradeId)
                                        : "SalaryData with id [%s] not found".formatted(id))
                                .build()
                )));
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a salary data entry by its ID.",
            summary = "Delete salary data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted the salary data"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary data not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    public ResponseEntity<StandardApiResponse<Void>> delete(@PathVariable @NotNull String salaryGradeId, @PathVariable @NotNull String id) {
        log.debug("Request to delete salaryData with id {}", id);
        var isDeleted = salaryDataService.delete(id, salaryGradeId);
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
