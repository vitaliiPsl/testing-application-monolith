package com.example.testing.repository;

import com.example.testing.model.User;
import com.example.testing.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndRole(String id, UserRole role);
}
