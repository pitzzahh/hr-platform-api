package dev.araopj.hrplatformapi.salary.controller;

import dev.araopj.hrplatformapi.salary.dto.request.SalaryGradeRequest;
import dev.araopj.hrplatformapi.salary.dto.response.SalaryGradeResponse;
import dev.araopj.hrplatformapi.salary.service.SalaryGradeService;
import dev.araopj.hrplatformapi.utils.StandardApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Salary Grade",
        description = "Endpoints for managing salary grades"
)
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/salary-grades")
public class SalaryGradeController {

    private final SalaryGradeService salaryGradeService;

    @GetMapping
    public ResponseEntity<StandardApiResponse<List<SalaryGradeResponse>>> all(
            @RequestParam(defaultValue = "false", required = false) @Valid boolean includeSalaryData
    ) {
        return ResponseEntity.ok(StandardApiResponse.success(salaryGradeService.findAll(
                includeSalaryData
        )));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> get(
            @PathVariable @NotBlank @Valid String id,
            @RequestParam(defaultValue = "false", required = false) @Valid boolean includeSalaryData
    ) {
        var response = salaryGradeService.findById(id, includeSalaryData);

        return response
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

    @PostMapping
    public ResponseEntity<StandardApiResponse<SalaryGradeResponse>> create(
            @RequestBody @Valid SalaryGradeRequest salaryGradeRequest,
            @RequestParam(defaultValue = "false", required = false) boolean includeSalaryData
    ) throws BadRequestException {
        log.debug("Request to create salaryGradeRequest: {}", salaryGradeRequest);
        return salaryGradeService.create(salaryGradeRequest, includeSalaryData)
                .map(StandardApiResponse::success)
                .map(ResponseEntity::ok)
                .orElseThrow(); // Exception handled globally
    }

}
