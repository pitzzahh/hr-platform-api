package dev.araopj.hrplatformapi.user.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Role extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
    String id;

    @Column(nullable = false)
    UserRole role = UserRole.EMPLOYEE;

    @Column(nullable = false, unique = true)
    String description;

    @Column(nullable = false)
    byte maxUser = 1; // Number of users that can be assigned to this role

    @OneToOne(mappedBy = "role", cascade = CascadeType.ALL)
    User user;
}
