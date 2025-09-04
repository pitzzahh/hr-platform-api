package dev.araopj.hrplatformapi.user.repository;

import dev.araopj.hrplatformapi.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User,String> {
}
