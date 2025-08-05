package com.patrick.personalfinance.repository;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findByEmail(String email);
}
