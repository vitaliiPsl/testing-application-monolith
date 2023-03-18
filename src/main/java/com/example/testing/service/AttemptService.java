package com.example.testing.service;

import com.example.testing.model.User;
import com.example.testing.payload.attempt.AttemptDto;
import com.example.testing.payload.attempt.AttemptResultDto;

/**
 * Test attempt service
 */
public interface AttemptService {

    /**
     * Process test attempt and return result
     *
     * @param subjectId  is of the subject
     * @param testId     id of the test
     * @param attemptDto attempt details
     * @param user       authenticated user
     * @return attempt result
     */
    AttemptResultDto processAttempt(String subjectId, String testId, AttemptDto attemptDto, User user);
}
