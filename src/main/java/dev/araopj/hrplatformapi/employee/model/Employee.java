package dev.araopj.hrplatformapi.employee.model;

import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Employee extends EntityTimestamp implements Serializable {

    @Id
    @Uuid
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
    String email;

    @Column
    String phoneNumber;

    @Enumerated(EnumType.STRING)
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

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Identifier> identifiers = new HashSet<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    Set<EmploymentInformation> employmentInformation;
}
