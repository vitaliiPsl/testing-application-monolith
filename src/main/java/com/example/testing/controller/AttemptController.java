package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.payload.attempt.AttemptResultDto;
import com.example.testing.service.AttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attempts")
public class AttemptController {
    private final AttemptService attemptService;

    @GetMapping("{attemptId}")
    AttemptResultDto getAttemptById(@PathVariable String attemptId, @AuthenticationPrincipal User user) {
        return attemptService.getAttemptById(attemptId, user);
    }

    @GetMapping
    List<AttemptResultDto> getAttemptsByTestId(@RequestParam(required = false) String testId, @AuthenticationPrincipal User user) {
        if(testId != null) {
            return attemptService.getAttemptsByTestId(testId, user);
        }

        return attemptService.getAttemptsByUser(user);
    }
}
