package dev.araopj.hrplatformapi.employee.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.araopj.hrplatformapi.utils.EntityTimestamp;
import dev.araopj.hrplatformapi.utils.annotations.Uuid;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
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
    private String id;

    @Column(unique = true, nullable = false)
    private Long employeeNumber;

    @Column(nullable = false)
    private String itemNumber;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String photo;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String taxPayerIdentificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CivilStatus civilStatus;

    @Column
    private String bankAccountNumber;

    @Column
    boolean archived;

    @Column
    private String userId;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IdDocument> idDocuments;

    @JsonIgnore
    @JsonManagedReference
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<EmploymentInformation> employmentInformation;
}
