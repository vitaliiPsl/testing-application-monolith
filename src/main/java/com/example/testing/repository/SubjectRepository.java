package com.example.testing.repository;

import com.example.testing.model.Subject;
import com.example.testing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {

    Optional<Subject> findByIdAndDeletedAtIsNull(String id);

    List<Subject> findALlByDeletedAtIsNull();

    List<Subject> findAllByEducatorAndDeletedAtIsNull(User user);
}
