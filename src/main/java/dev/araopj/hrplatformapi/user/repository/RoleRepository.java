package dev.araopj.hrplatformapi.user.repository;

import dev.araopj.hrplatformapi.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
}
