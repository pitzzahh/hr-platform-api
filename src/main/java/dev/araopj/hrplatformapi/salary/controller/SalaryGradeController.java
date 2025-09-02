package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.exception.NotFoundException;
import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.service.SalaryGradeService;
import dev.araopj.hrplatformapi.utils.ApiError;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing salary grades.
 * Provides endpoints for retrieving and creating salary grade entries.
 */
@Tag(
        name = "Salary Grade",
        description = """
                Endpoints for managing salary grades, including operations to retrieve and create salary grade entries.
                """
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/salary-grades")
public class SalaryGradeController {

    private final SalaryGradeService salaryGradeService;

    /**
     * Retrieves a list of all salary grade entries.
     *
     * @param includeSalaryData If true, includes associated salary data in the response.
     * @return A ResponseEntity containing a StandardApiResponse with a list of SalaryGradeResponse objects.
     */
    @Operation(
            summary = "Get all salary grades",
            description = """
                    Retrieve a list of all salary grade entries. 
                    Optionally include associated salary data using the 'includeSalaryData' parameter.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of salary grades",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<StandardApiResponse<List<SalaryGradeResponse>>> all(
            @Parameter(description = "Include associated salary data in the response", example = "false")
            @RequestParam(defaultValue = "false", required = false) @Valid boolean includeSalaryData
    ) {
        log.debug("Fetching all salary grades with includeSalaryData: {}", includeSalaryData);
        return ResponseEntity.ok(StandardApiResponse.success(salaryGradeService.findAll(includeSalaryData)));
    }

    /**
     * Retrieves a specific salary grade entry by its ID.
     *
     * @param id                The ID of the salary grade to retrieve.
     * @param includeSalaryData If true, includes associated salary data in the response.
     * @return A ResponseEntity containing a StandardApiResponse with the SalaryGradeResponse.
     * @throws NotFoundException If the salary grade is not found.
     */
    @Operation(
            summary = "Get salary grade by ID",
            description = """
                    Retrieve a specific salary grade entry by its ID. 
                    Optionally include associated salary data using the 'includeSalaryData' parameter.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Salary grade not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> get(
            @Parameter(description = "ID of the salary grade to retrieve", required = true)
            @PathVariable @NotBlank String id,
            @Parameter(description = "Include associated salary data in the response", example = "false")
            @RequestParam(defaultValue = "false", required = false) @Valid boolean includeSalaryData
    ) {
        log.debug("Fetching salary grade with id: {}, includeSalaryData: {}", id, includeSalaryData);
        var response = salaryGradeService.findById(id, includeSalaryData);
        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    /**
     * Creates a new salary grade entry.
     *
     * @param salaryGradeRequest The salary grade details to create.
     * @param includeSalaryData  If true, includes associated salary data in the creation process.
     * @return A ResponseEntity containing a StandardApiResponse with the created SalaryGradeResponse.
     * @throws BadRequestException If the salary grade already exists with the same salary grade and effective date.
     */
    @Operation(
            summary = "Create salary grade",
            description = """
                    Create a new salary grade entry. 
                    Optionally include associated salary data using the 'includeSalaryData' parameter.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created the salary grade",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StandardApiResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid salary grade data provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Salary grade with the same salary grade and effective date already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class)
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> create(
            @Parameter(description = "Salary grade details to create", required = true)
            @RequestBody @Valid SalaryGradeRequest salaryGradeRequest,
            @Parameter(description = "Include associated salary data in the creation process", example = "false")
            @RequestParam(defaultValue = "false", required = false) boolean includeSalaryData
    ) throws BadRequestException {
        log.debug("Request to create salaryGradeRequest: {}", salaryGradeRequest);
        return salaryGradeService.create(salaryGradeRequest, includeSalaryData)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }
}