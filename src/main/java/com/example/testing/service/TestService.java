package com.example.testing.service;

import com.example.testing.model.User;
import com.example.testing.model.test.Test;
import com.example.testing.payload.test.TestDto;

import java.util.List;

/**
 * Test service
 */
public interface TestService {

    /**
     * Save given test
     *
     * @param req  request payload
     * @param user authenticated user
     * @return saved test
     */
    TestDto saveTest(TestDto req, User user);

    /**
     * Update given test
     *
     * @param testId id of the test
     * @param req    request payload
     * @param user   authenticated user
     * @return updated test
     */
    TestDto updateTest(String testId, TestDto req, User user);

    /**
     * Delete given test softly
     *
     * @param testId id of the test
     * @param user   authenticated user
     */
    void deleteTest(String testId, User user);

    /**
     * Get test with given id
     *
     * @param testId id of the test
     * @return retrieved test
     */
    TestDto getTestById(String testId);

    /**
     * Get tests for given subjects
     *
     * @param subjectId id of the subject
     * @return retrieved tests
     */
    List<TestDto> getTestsBySubjectId(String subjectId);

    /**
     * Get test entity
     *
     * @param testId    id of the test
     * @return fetched test entity
     */
    Test getTestEntity(String testId);
}
