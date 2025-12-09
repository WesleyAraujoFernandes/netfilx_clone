package com.netflix.clone.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.netflix.clone.entity.User;
import com.netflix.clone.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    long countByRoleAndActive(Role role, boolean active);

    @Query("SELECT u FROM User u WHERE "
            + "LOWER(u.fullName) LIKE LOWER (CONCAT('%',:search,'%')) OR "
            + "LOWER(u.email) LIKE LOWER(CONCAT('%',:search,'%'))")
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
}
