package dev.araopj.hrplatformapi.user.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Permission extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
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
}
