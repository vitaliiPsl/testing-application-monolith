package com.example.testing.service.impl;

import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.Subject;
import com.example.testing.model.User;
import com.example.testing.model.test.Option;
import com.example.testing.model.test.Question;
import com.example.testing.model.test.Test;
import com.example.testing.payload.test.OptionDto;
import com.example.testing.payload.test.QuestionDto;
import com.example.testing.payload.test.TestDto;
import com.example.testing.repository.TestRepository;
import com.example.testing.service.SubjectService;
import com.example.testing.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TestServiceImpl implements TestService {
    private static final int MIN_NUMBER_OF_OPTIONS = 2;
    private static final int MIN_NUMBER_OF_CORRECT_OPTIONS = 1;
    private final TestRepository testRepository;
    private final SubjectService subjectService;
    private final ModelMapper mapper;

    private static Test createTest(TestDto testDto, Subject subject) {
        return Test.builder()
                .subject(subject)
                .name(testDto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public TestDto saveTest(String subjectId, TestDto req, User user) {
        log.debug("Save test {} for subject {}", req, subjectId);

        // get subject
        Subject subject = subjectService.getSubjectEntityAndVerifyEducator(subjectId, user);

        // map test dto to test
        Test test = createTest(req, subject);

        // map question dtos to questions
        Set<Question> questions = createQuestions(req.getQuestions(), test);
        test.setQuestions(questions);

        // save test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    @Override
    public TestDto updateTest(String subjectId, String testId, TestDto req, User user) {
        log.debug("Update test with subject id {} and test id {}. Update data: {}", subjectId, testId, req);

        Subject subject = subjectService.getSubjectEntityAndVerifyEducator(subjectId, user);

        // fetch test
        Test test = testRepository.findByIdAndSubject(testId, subject)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        // TODO: verify that no one has already taken the test

        // update test properties
        test.setName(req.getName());
        test.setUpdatedAt(LocalDateTime.now());

        // map questions
        Set<Question> questions = createQuestions(req.getQuestions(), test);
        test.setQuestions(questions);

        // save updated test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    private Set<Question> createQuestions(Set<QuestionDto> questionDtos, Test test) {
        return questionDtos.stream().map(questionDto -> createQuestion(questionDto, test)).collect(Collectors.toSet());
    }

    private Question createQuestion(QuestionDto questionDto, Test test) {
        Question question = Question.builder()
                .test(test)
                .question(questionDto.getQuestion())
                .build();

        Set<Option> options = createOptions(questionDto.getOptions(), question);
        if (options.size() < MIN_NUMBER_OF_OPTIONS) {
            log.error("Requires at least {} option. Received: {}", MIN_NUMBER_OF_OPTIONS, options.size());
            throw new IllegalStateException(
                    String.format("There must be at least %s option for question: '%s'\n", MIN_NUMBER_OF_OPTIONS, question.getQuestion())
            );
        }

        question.setOptions(options);
        if (question.getCorrectOptions().size() < 1) {
            log.error("Requires at least one correct option. Received: {}", question.getCorrectOptions().size());
            throw new IllegalStateException(
                    String.format("There must be at least %s correct option for question: '%s'\n", MIN_NUMBER_OF_CORRECT_OPTIONS, question.getQuestion())
            );
        }

        return question;
    }

    private Set<Option> createOptions(Set<OptionDto> optionDtos, Question question) {
        return optionDtos.stream().map(optionDto -> createOption(optionDto, question)).collect(Collectors.toSet());
    }

    private Option createOption(OptionDto optionDto, Question question) {
        return Option.builder()
                .question(question)
                .option(optionDto.getOption())
                .correct(optionDto.isCorrect())
                .build();
    }

    private TestDto mapTestToTestDto(Test test) {
        return mapper.map(test, TestDto.class);
    }
}
