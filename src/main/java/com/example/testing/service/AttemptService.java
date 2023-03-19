package com.example.testing.service;

import com.example.testing.model.User;
import com.example.testing.payload.attempt.AttemptDto;
import com.example.testing.payload.attempt.AttemptResultDto;

import java.util.List;

/**
 * Test attempt service
 */
public interface AttemptService {

    /**
     * Process test attempt and return result
     *
     * @param testId     id of the test
     * @param attemptDto attempt details
     * @param user       authenticated user
     * @return attempt result
     */
    AttemptResultDto processAttempt(String testId, AttemptDto attemptDto, User user);

    /**
     * Get attempt result by id
     *
     * @param attemptId if of the attempt
     * @param user      authenticated user
     * @return fetched attempt
     */
    AttemptResultDto getAttemptById(String attemptId, User user);

    /**
     * Get attempts by id of the test and verify that user is the educator of the test subject
     *
     * @param testId id of the test
     * @param user   authenticated user
     * @return list of attempt
     */
    List<AttemptResultDto> getAttemptsByTestId(String testId, User user);

    /**
     * Get attempts by user
     * @param user authenticated user
     * @return list of attempts
     */
    List<AttemptResultDto> getAttemptsByUser(User user);
}
