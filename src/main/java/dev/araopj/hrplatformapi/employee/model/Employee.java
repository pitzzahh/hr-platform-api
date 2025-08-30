package dev.araopj.hrplatformapi.employee.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(unique = true, nullable = false)
    Long employeeNumber;

    @Column(nullable = false)
    String itemNumber;

    @Column(nullable = false)
    String firstName;

    @Column
    String middleName;

    @Column(nullable = false)
    String lastName;

    @Column
    String photo;

    @Column(nullable = false)
    LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Email must be a valid email address"
    )
    String email;

    @Column
    @Pattern(
            regexp = "^(09\\d{9}|9\\d{9}|\\+639\\d{9})$",
            message = "Phone number must be: 11 digits starting with 09, or 10 digits starting with 9, or 13 characters starting with +639"
    )
    String phoneNumber;

    @Column(nullable = false)
    Gender gender;

    @Column(nullable = false)
    String taxPayerIdentificationNumber;

    @Column(nullable = false)
    CivilStatus civilStatus;

    @Column
    String bankAccountNumber;

    @Column
    boolean archived;

    @Column
    String userId;

    @OneToOne
    @JoinColumn(name = "gsis_id", nullable = false, unique = true)
    Gsis gsis;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    Set<EmploymentInformation> employmentInformation;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
