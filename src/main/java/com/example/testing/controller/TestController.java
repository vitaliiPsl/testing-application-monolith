package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.payload.test.TestDto;
import com.example.testing.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subjects/{subjectId}/tests")
public class TestController {

    private final TestService testService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    TestDto saveTest(
            @PathVariable String subjectId,
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal User user
    ) {
        return testService.saveTest(subjectId, req, user);
    }

    @PutMapping("{testId}")
    TestDto updateTest(
            @PathVariable String subjectId, @PathVariable String testId,
            @RequestBody @Valid TestDto req, @AuthenticationPrincipal User user
    ) {
        return testService.updateTest(subjectId, testId, req, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{testId}")
    void deleteTest(
            @PathVariable String subjectId, @PathVariable String testId,
            @AuthenticationPrincipal User user
    ) {
        testService.deleteTest(subjectId, testId, user);
    }

    @GetMapping("{testId}")
    TestDto getTests(@PathVariable String subjectId, @PathVariable String testId) {
        return testService.getTestById(subjectId, testId);
    }

    @GetMapping
    List<TestDto> getTestById(@PathVariable String subjectId) {
        return testService.getTestsBySubjectId(subjectId);
    }
}
