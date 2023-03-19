package com.example.testing.repository;

import com.example.testing.model.Subject;
import com.example.testing.model.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, String> {
    Optional<Test> findByIdAndDeletedAtIsNull(String testId);

    List<Test> findBySubjectAndDeletedAtIsNull(Subject subject);
}
