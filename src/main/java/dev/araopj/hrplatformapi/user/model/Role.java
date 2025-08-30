package dev.araopj.hrplatformapi.user.model;

import jakarta.persistence.*;
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
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false)
    UserRole role = UserRole.EMPLOYEE;

    @Column(nullable = false, unique = true)
    String description;

    @Column(nullable = false)
    byte maxUser = 1; // Number of users that can be assigned to this role

    @OneToOne(mappedBy = "role", cascade = CascadeType.ALL)
    User user;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
