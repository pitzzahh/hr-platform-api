package dev.araopj.hrplatformapi.user.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.Uuid;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User extends EntityTimestamp implements Serializable { // TODO: implement UserDetails interface for Spring Security

    @Id
    @Uuid
    String id;

    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must be a valid email address"
    )
    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false)
    String password;

    @OneToOne
    @JoinColumn(name = "role_id", nullable = false, unique = true)
    Role role;

    @Column
    String photo;

    @Column
    String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserStatus status = UserStatus.DISABLED;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column
    String totp_key;

    @Column
    boolean registered_two_factor;

    @Column
    boolean email_verified;

    @Column
    boolean archived;
}
