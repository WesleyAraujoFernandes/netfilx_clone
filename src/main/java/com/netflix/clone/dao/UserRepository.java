package com.netflix.clone.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    long countByRoleAndActive(Role role, boolean active);
}
