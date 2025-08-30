package dev.araopj.hrplatformapi.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false)
    @Pattern(
        regexp = "^[A-Z_]+$",
        message = "Permission code must be uppercase letters and underscores only"
    )
    String code;

    @Column(nullable = false, length = 100)
    @Size(min = 5, max = 100, message = "Permission description must be between 5 and 100 characters")
    String description;

    @Column(nullable = false)
    @Pattern(
        regexp = "^[A-Z_]+$",
        message = "Permission category must be uppercase letters and underscores only"
    )
    String category;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
