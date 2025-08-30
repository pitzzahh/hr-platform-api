package dev.araopj.hrplatformapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

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
    private String details;
}