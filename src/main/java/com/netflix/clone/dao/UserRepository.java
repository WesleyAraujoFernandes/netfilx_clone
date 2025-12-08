package com.netflix.clone.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.netflix.clone.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
