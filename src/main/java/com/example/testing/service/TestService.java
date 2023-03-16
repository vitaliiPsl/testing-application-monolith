package com.example.testing.service;

import com.example.testing.model.User;
import com.example.testing.payload.test.TestDto;

/**
 * Test service
 */
public interface TestService {

    /**
     * Save given test and assign it to the subject with given id
     *
     * @param subjectId id of the subject
     * @param req       request payload
     * @param user      authenticated user
     * @return saved test
     */
    TestDto saveTest(String subjectId, TestDto req, User user);

    /**
     * Update given test
     *
     * @param subjectId id of the subject
     * @param testId    id of the test
     * @param req       request payload
     * @param user      authenticated user
     * @return updated test
     */
    TestDto updateTest(String subjectId, String testId, TestDto req, User user);
}
