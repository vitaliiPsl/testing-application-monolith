package com.example.testing.service.impl;

import com.example.testing.exceptions.ForbiddenException;
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
import java.util.List;
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

    @Override
    public TestDto saveTest(TestDto req, User user) {
        log.debug("Save test {}", req);

        // get subject
        Subject subject = subjectService.getSubjectEntityAndVerifyEducator(req.getSubjectId(), user);

        // map test dto to test
        Test test = createTest(req, subject);

        // map question dtos to questions
        Set<Question> questions = createQuestions(req.getQuestions());
        test.setQuestions(questions);

        // save test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    @Override
    public TestDto updateTest(String testId, TestDto req, User user) {
        log.debug("Update test with id {}. Update data: {}", testId, req);

        // fetch test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        // verify that user is the educator of the test subject
        if(!test.getSubject().getEducator().equals(user)) {
            log.error("User is not an educator of the subject: {}", test.getSubject());
            throw new ForbiddenException("Not an educator of the subject: " + test.getSubject().getId());
        }

        if (test.getAttempts().size() != 0) {
            log.error("There are already test attempts");
            throw new IllegalStateException("Test has been already taken");
        }

        // update test properties
        test.setName(req.getName());
        test.setUpdatedAt(LocalDateTime.now());

        // map questions
        Set<Question> questions = createQuestions(req.getQuestions());
        test.setQuestions(questions);

        // save updated test
        test = testRepository.save(test);
        return mapTestToTestDto(test);
    }

    @Override
    public void deleteTest(String testId, User user) {
        log.debug("Delete test with id {}", testId);

        // fetch test
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        // verify that user is the educator of the test subject
        if(!test.getSubject().getEducator().equals(user)) {
            log.error("User is not an educator of the subject: {}", test.getSubject());
            throw new ForbiddenException("Not an educator of the subject: " + test.getSubject().getId());
        }

        testRepository.delete(test);
    }

    @Override
    public TestDto getTestById(String testId) {
        log.debug("Get test with id: {}", testId);

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));

        return mapTestToTestDto(test);
    }

    @Override
    public List<TestDto> getTestsBySubjectId(String subjectId) {
        log.debug("Get tests by subject with id: {}", subjectId);

        Subject subject = subjectService.getSubjectEntity(subjectId);

        return testRepository.findBySubject(subject)
                .stream().map(this::mapTestToTestDto).collect(Collectors.toList());
    }

    @Override
    public Test getTestEntity(String testId) {
        return testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("test", "id", testId));
    }

    private static Test createTest(TestDto testDto, Subject subject) {
        return Test.builder()
                .subject(subject)
                .name(testDto.getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Set<Question> createQuestions(Set<QuestionDto> questionDtos) {
        return questionDtos.stream().map(this::createQuestion).collect(Collectors.toSet());
    }

    private Question createQuestion(QuestionDto questionDto) {
        Question question = Question.builder()
                .question(questionDto.getQuestion())
                .build();

        Set<Option> options = createOptions(questionDto.getOptions());
        if (options.size() < MIN_NUMBER_OF_OPTIONS) {
            log.error("Requires at least {} option. Received: {}", MIN_NUMBER_OF_OPTIONS, options.size());
            throw new IllegalStateException(
                    String.format("There must be at least %s option for question: '%s'", MIN_NUMBER_OF_OPTIONS, question.getQuestion())
            );
        }

        question.setOptions(options);
        if (question.getCorrectOptions().size() < 1) {
            log.error("Requires at least one correct option. Received: {}", question.getCorrectOptions().size());
            throw new IllegalStateException(
                    String.format("There must be at least %s correct option for question: '%s'", MIN_NUMBER_OF_CORRECT_OPTIONS, question.getQuestion())
            );
        }

        return question;
    }

    private Set<Option> createOptions(Set<OptionDto> optionDtos) {
        return optionDtos.stream().map(this::createOption).collect(Collectors.toSet());
    }

    private Option createOption(OptionDto optionDto) {
        return Option.builder()
                .option(optionDto.getOption())
                .correct(optionDto.isCorrect())
                .build();
    }

    private TestDto mapTestToTestDto(Test test) {
        return mapper.map(test, TestDto.class);
    }
}
