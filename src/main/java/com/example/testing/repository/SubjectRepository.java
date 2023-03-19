package com.example.testing.repository;

import com.example.testing.model.Subject;
import com.example.testing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {

    List<Subject> findAllByEducator(User user);
}
