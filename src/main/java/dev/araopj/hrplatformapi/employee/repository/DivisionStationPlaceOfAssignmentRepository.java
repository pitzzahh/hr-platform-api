package dev.araopj.hrplatformapi.employee.repository;

import dev.araopj.hrplatformapi.employee.model.DivisionStationPlaceOfAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionStationPlaceOfAssignmentRepository extends JpaRepository<DivisionStationPlaceOfAssignment, String> {
}
