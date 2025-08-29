package dev.araopj.hrplatformapi.employee.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "gsis")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Gsis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    String id;

    @Column(nullable = false, name = "business_partner_number", unique = true)
    String businessPartnerNumber;

    @Column
    LocalDate issuedDate;

    @Column
    String issuedPlace;

    @OneToOne(mappedBy = "gsis", cascade = CascadeType.ALL)
    Employee employee;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime created_at;

    @Column(nullable = false, updatable = false)
    @UpdateTimestamp
    private LocalDateTime updated_at;
}
