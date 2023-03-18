package com.example.testing.repository;

import com.example.testing.model.attempt.AttemptResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttemptResultRepository extends JpaRepository<AttemptResult, String> {
}
