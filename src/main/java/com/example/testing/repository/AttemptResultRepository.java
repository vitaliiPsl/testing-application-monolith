package com.example.testing.repository;

import com.example.testing.model.User;
import com.example.testing.model.attempt.AttemptResult;
import com.example.testing.model.test.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptResultRepository extends JpaRepository<AttemptResult, String> {

    List<AttemptResult> findByUser(User user);

    List<AttemptResult> findByTest(Test test);
}
