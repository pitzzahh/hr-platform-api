package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionStationPlaceOfAssignmentRepository extends JpaRepository<DivisionStationPlaceOfAssignment, String> {
    Optional<DivisionStationPlaceOfAssignment> findByIdAndEmploymentInformation_Id(String id, String employeeId);
}
