package com.example.testing.service.impl;

import com.example.testing.model.User;
import com.example.testing.model.attempt.AttemptAnswer;
import com.example.testing.model.attempt.AttemptQuestion;
import com.example.testing.model.attempt.AttemptResult;
import com.example.testing.model.test.Option;
import com.example.testing.model.test.Question;
import com.example.testing.model.test.Test;
import com.example.testing.payload.attempt.AttemptAnswerDto;
import com.example.testing.payload.attempt.AttemptDto;
import com.example.testing.payload.attempt.AttemptQuestionDto;
import com.example.testing.payload.attempt.AttemptResultDto;
import com.example.testing.repository.AttemptResultRepository;
import com.example.testing.service.AttemptService;
import com.example.testing.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AttemptServiceImpl implements AttemptService {
    private final AttemptResultRepository attemptRepository;
    private final TestService testService;
    private final ModelMapper mapper;

    @Override
    public AttemptResultDto processAttempt(String subjectId, String testId, AttemptDto attemptDto, User user) {
        log.debug("Process attempt of the test with subject id {} and test id {}. Attempt details: {}", subjectId, testId, attemptDto);

        // find test
        Test test = testService.getTestEntity(subjectId, testId);
        Set<Question> questions = test.getQuestions();

        // check answers
        Set<AttemptQuestion> attemptQuestions = checkQuestionsAnswers(questions, List.copyOf(attemptDto.getQuestions()));

        AttemptResult attempt = AttemptResult.builder()
                .user(user)
                .test(test)
                .createdAt(LocalDateTime.now())
                .build();

        // get score and assign attempt to attempt questions
        int score = 0;
        int maxScore = 0;
        for (var question: attemptQuestions) {
            question.setAttempt(attempt);
            score += question.getScore();
            maxScore += question.getMaxScore();
        }

        attempt.setAttemptQuestions(attemptQuestions);
        attempt.setScore(score);
        attempt.setMaxScore(maxScore);

        // save attempt
        attempt = attemptRepository.save(attempt);
        return mapAttemptResultToAttemptResultDto(attempt);
    }

    private Set<AttemptQuestion> checkQuestionsAnswers(Set<Question> questions, List<AttemptQuestionDto> attemptQuestions) {
        return questions.stream()
                .map(question -> checkQuestionAnswer(question, attemptQuestions))
                .collect(Collectors.toSet());
    }

    private AttemptQuestion checkQuestionAnswer(Question question, List<AttemptQuestionDto> attemptQuestions) {
        // get attempt for given question
        Optional<AttemptQuestionDto> questionAttemptDto = attemptQuestions.stream()
                .filter(attemptQuestion -> question.getId().equals(attemptQuestion.getQuestionId())).findFirst();

        // get answers for given question
        Set<AttemptAnswer> answers = Set.of();
        if (questionAttemptDto.isPresent()) {
            answers = checkQuestionAnswer(question, questionAttemptDto.get());
        }

        AttemptQuestion attemptQuestion = AttemptQuestion.builder().question(question)
                .maxScore(question.getCorrectOptions().size()).build();

        // set answer question and count correct answers
        int correctAnswers = 0;
        for (var answer: answers) {
            answer.setQuestion(attemptQuestion);
            correctAnswers += answer.isCorrect() ? 1 : 0;
        }

        attemptQuestion.setAnswers(answers);
        attemptQuestion.setScore(correctAnswers);

        return attemptQuestion;
    }

    private Set<AttemptAnswer> checkQuestionAnswer(Question question, AttemptQuestionDto attemptQuestion) {
        Set<AttemptAnswerDto> attemptQuestionAnswers = attemptQuestion.getAnswers();

        if (attemptQuestionAnswers.size() > question.getCorrectOptions().size()) {
            log.error("Possible number of answers: {}, received {}", question.getCorrectOptions().size(), attemptQuestionAnswers.size());
            throw new IllegalStateException("Invalid number of answers. Require no more than " + question.getCorrectOptions().size());
        }

        // collect options to map with ids as keys and options as values
        Map<Long, Option> options = question.getOptions().stream()
                .collect(Collectors.toMap(Option::getId, Function.identity()));

        return attemptQuestionAnswers.stream()
                .map(answer -> checkQuestionAnswer(answer, options)).collect(Collectors.toSet());
    }

    private AttemptAnswer checkQuestionAnswer(AttemptAnswerDto answer, Map<Long, Option> options) {
        Option option = options.get(answer.getOptionId());

        if (option == null) {
            log.error("There is not such option for given question: {}", answer.getOptionId());
            throw new IllegalStateException("No such option: " + answer.getOptionId());
        }

        return AttemptAnswer.builder()
                .option(option)
                .correct(option.isCorrect())
                .build();
    }

    private AttemptResultDto mapAttemptResultToAttemptResultDto(AttemptResult attemptResult) {
        return mapper.map(attemptResult, AttemptResultDto.class);
    }
}
