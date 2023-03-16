package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.payload.test.TestDto;
import com.example.testing.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
}
