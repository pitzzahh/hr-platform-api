package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NonNullApi
@Repository
public interface DivisionStationPlaceOfAssignmentRepository extends JpaRepository<DivisionStationPlaceOfAssignment, String> {
    Optional<DivisionStationPlaceOfAssignment> findByIdAndEmploymentInformationId(String id, String employeeId);

    Optional<DivisionStationPlaceOfAssignment> findByCodeAndNameAndEmploymentInformationId(String code, String name, String employmentInformationId);

    @EntityGraph(attributePaths = "employmentInformation")
    @Override
    Page<DivisionStationPlaceOfAssignment> findAll(Pageable pageable);
}
