package dev.araopj.hrplatformapi.user.repository;

import dev.araopj.hrplatformapi.user.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface PermissionRepository extends JpaRepository<Permission, String> {
}
