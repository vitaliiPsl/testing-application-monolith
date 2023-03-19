package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.payload.attempt.AttemptDto;
import com.example.testing.payload.attempt.AttemptResultDto;
import com.example.testing.payload.test.TestDto;
import com.example.testing.service.AttemptService;
import com.example.testing.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;
    private final AttemptService attemptService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    TestDto saveTest(
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal User user
    ) {
        return testService.saveTest(req, user);
    }

    @PutMapping("{testId}")
    TestDto updateTest(
            @PathVariable String testId,
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal User user
    ) {
        return testService.updateTest(testId, req, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{testId}")
    void deleteTest(
            @PathVariable String testId,
            @AuthenticationPrincipal User user
    ) {
        testService.deleteTest(testId, user);
    }

    @GetMapping("{testId}")
    TestDto getTests(@PathVariable String testId) {
        return testService.getTestById(testId);
    }

    @GetMapping(params = "subjectId")
    List<TestDto> getTestBySubjectId(@RequestParam String subjectId) {
        return testService.getTestsBySubjectId(subjectId);
    }

    @PostMapping("{testId}/attempts")
    AttemptResultDto takeTest(
            @PathVariable String testId,
            @RequestBody @Valid AttemptDto attemptDto,
            @AuthenticationPrincipal User user
    ) {
        return attemptService.processAttempt(testId, attemptDto, user);
    }
}
