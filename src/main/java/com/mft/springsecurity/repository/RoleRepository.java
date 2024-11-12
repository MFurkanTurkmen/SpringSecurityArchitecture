package com.mft.springsecurity.repository;

import com.mft.springsecurity.entity.Role;
import com.mft.springsecurity.entity.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findOptionalByName(ERole name);
}
