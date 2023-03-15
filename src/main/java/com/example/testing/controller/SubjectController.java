package com.example.testing.controller;

import com.example.testing.model.User;
import com.example.testing.payload.SubjectDto;
import com.example.testing.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    SubjectDto saveSubject(@RequestBody @Valid SubjectDto req, @AuthenticationPrincipal User user) {
        return subjectService.saveSubject(req, user);
    }

    @PutMapping("{id}")
    SubjectDto updateSubject(
            @PathVariable String id,
            @RequestBody @Valid SubjectDto req,
            @AuthenticationPrincipal User user
    ) {
        return subjectService.updateSubject(id, req, user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    void deleteSubject(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        subjectService.deleteSubject(id, user);
    }

    @GetMapping("{id}")
    SubjectDto getSubjectById(@PathVariable String id) {
        return subjectService.getSubjectById(id);
    }

    @GetMapping
    List<SubjectDto> getSubjectsByEducatorId(@RequestParam(required = false) String educatorId) {
        if (educatorId != null) {
            return subjectService.getSubjectsByEducatorId(educatorId);
        }

        return subjectService.getAllSubjects();
    }
}
