package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryDataRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryDataResponse;
import dev.araopj.hrplatformapi.salary.service.SalaryDataService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import dev.araopj.hrplatformapi.utils.enums.CheckType;
import dev.araopj.hrplatformapi.utils.enums.CreateType;
import dev.araopj.hrplatformapi.utils.enums.FetchType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Limit;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.araopj.hrplatformapi.utils.enums.FetchType.WITH_PARENT_REQUEST_PARAM;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/v1/salary-data")
@Tag(
        name = "Salary Data",
        description = "Endpoints for managing salary data associated with specific salary grades."
)
public class SalaryDataController {

    private final SalaryDataService salaryDataService;

    @Operation(
            description = """
                    Retrieve a list of all salary data entries for a specific salary grade.
                    Optionally limit the number of results returned using the <i>limit</i> query parameter.
                    You can also get salary data associated with a specific salary grade by providing the <i>salaryGradeId</i> query parameter.
                    """,
            summary = "Get all salary data",
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
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<SalaryDataResponse>>> all(
            @RequestParam(required = false) String salaryGradeId,
            @RequestParam(defaultValue = "10", required = false) @Valid int limit
    ) {
        return ResponseEntity.ok(StandardApiResponse.success(salaryDataService.findAll(
                salaryGradeId,
                Limit.of(limit)
        )));
    }

    @Operation(
            description = """
                    Get a specific salary data entry by its ID.
                    Optionally, you can provide a salary grade ID to ensure the salary data belongs to that specific grade.
                    You can also choose different fetching strategies using the <i>fetchType</i> query parameter.
                    """,
            summary = "Get salary data by ID",
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
    @GetMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> get(
            @PathVariable String id,
            @RequestParam(required = false) String salaryGradeId,
            @RequestParam(defaultValue = "WITH_PARENT_REQUEST_PARAM", required = false) @Valid FetchType fetchType
    ) {
        var response = fetchType == WITH_PARENT_REQUEST_PARAM
                ? salaryDataService.findByIdAndSalaryGradeId(id, salaryGradeId)
                : salaryDataService.findById(id);

        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    @Operation(
            description = """
                    Create a new salary data entry for a specific salary grade.
                    Allows optional validation to check if the parent salary grade ID should be taken from the request body
                    or from the query parameter.
                    """,
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
    @PostMapping
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> create(
            @RequestBody @Valid SalaryDataRequest salaryDataRequest,
            @RequestParam(required = false) String salaryGradeId,
            @RequestParam(defaultValue = "CHECK_PARENT_FROM_REQUEST_BODY", required = false) @Valid CheckType checkType
    ) throws BadRequestException {
        log.debug("Request to create salaryDataRequest: {}", salaryDataRequest);
        return salaryDataService.create(salaryDataRequest, salaryGradeId, checkType)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    @Operation(
            description = """
                    Update an existing salary data entry by its ID for a specific salary grade.
                    Allows optional fetching strategies and the choice to use the parent salary grade ID from the path variable
                    or from the request body.
                    """,
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
                            description = "Salary data to update not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryDataResponse>> update(
            @PathVariable @NotNull String id,
            @RequestParam @NotNull String salaryGradeId,
            @RequestBody @Valid SalaryDataRequest salaryDataRequest,
            @RequestParam(defaultValue = "FROM_PATH_VARIABLE") CreateType createType,
            @RequestParam(defaultValue = "false") boolean useParentIdFromPathVariable
    ) {
        log.info("Request to update salary data with id {}: {}", id, salaryGradeId);
        return ResponseEntity
                .ok(StandardApiResponse
                        .success(salaryDataService.update(
                                        id,
                                        salaryGradeId,
                                        salaryDataRequest,
                                        createType,
                                        useParentIdFromPathVariable
                                )
                        )
                );

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
