package dev.araopj.hrplatformapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    @NotNull
    private String message;
    @Null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> details;
}