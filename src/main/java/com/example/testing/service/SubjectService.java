package com.example.testing.service;

import com.example.testing.model.User;
import com.example.testing.payload.SubjectDto;

import java.util.List;

/**
 * Subject service
 */
public interface SubjectService {

    /**
     * Save given subject
     *
     * @param req  subject dto
     * @param user authenticated user
     * @return saved subject
     */
    SubjectDto saveSubject(SubjectDto req, User user);

    /**
     * Update subject with given id
     *
     * @param subjectId id of the subject
     * @param req       subject dto
     * @param user      authenticated user
     * @return updated subject
     */
    SubjectDto updateSubject(String subjectId, SubjectDto req, User user);

    /**
     * Delete subject with given id
     *
     * @param subjectId id of the subject
     * @param user      authenticated user
     */
    void deleteSubject(String subjectId, User user);

    /**
     * Get subject with given id
     *
     * @param subjectId id of the subject
     * @return fetched subject
     */
    SubjectDto getSubjectById(String subjectId);

    /**
     * Get all subjects
     *
     * @return list of the fetched subjects
     */
    List<SubjectDto> getAllSubjects();

    /**
     * Get subjects by id of the educator
     *
     * @param educatorId id of the educator
     * @return list of the subjects of given educator
     */
    List<SubjectDto> getSubjectsByEducatorId(String educatorId);
}
