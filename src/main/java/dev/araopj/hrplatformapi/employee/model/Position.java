package dev.araopj.hrplatformapi.employee.model;


import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Position extends EntityTimestamp {

    @Id
    
    @GeneratedValue
    String id;

    @Column(nullable = false, unique = true)
    String code;

    @Column(nullable = false, unique = true)
    String description;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
