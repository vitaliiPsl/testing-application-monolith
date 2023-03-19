package com.example.testing.service.impl;

import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.User;
import com.example.testing.model.attempt.AttemptResult;
import com.example.testing.model.test.Option;
import com.example.testing.model.test.Question;
import com.example.testing.model.test.Test;
import com.example.testing.payload.attempt.AttemptAnswerDto;
import com.example.testing.payload.attempt.AttemptDto;
import com.example.testing.payload.attempt.AttemptQuestionDto;
import com.example.testing.payload.attempt.AttemptResultDto;
import com.example.testing.repository.AttemptResultRepository;
import com.example.testing.service.TestService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttemptServiceImplTest {
    @Mock
    AttemptResultRepository attemptRepository;
    @Mock
    TestService testService;
    @Spy
    ModelMapper mapper;

    @InjectMocks
    AttemptServiceImpl attemptService;

    @Captor
    ArgumentCaptor<AttemptResult> attemptCaptor;

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenAllAnswersCorrect_thenSaveAttemptResultWithMaxScore() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(4L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(testId, attemptDto, user);

        // then
        verify(testService).getTestEntity(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUser(), is(user));
        assertThat(attemptResult.getTest(), is(test));
        assertThat(attemptResult.getScore(), is(3));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(3));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenOnlyOneCorrectAnswerToQuestionWithTwoCorrectAnswers_thenSaveAttemptResultWithScore2OutOf3() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(testId, attemptDto, user);

        // then
        verify(testService).getTestEntity(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUser(), is(user));
        assertThat(attemptResult.getTest(), is(test));
        assertThat(attemptResult.getScore(), is(2));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(2));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenAllAnswersWrong_thenSaveAttemptResultWithZeroScore() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(1L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build()
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(testId, attemptDto, user);

        // then
        verify(testService).getTestEntity(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUser(), is(user));
        assertThat(attemptResult.getTest(), is(test));
        assertThat(attemptResult.getScore(), is(0));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(0));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenAnswers_thenSaveAttemptResultWithZeroScore() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of())
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of())
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);
        when(attemptRepository.save(Mockito.any(AttemptResult.class))).then(AdditionalAnswers.returnsFirstArg());

        AttemptResultDto res = attemptService.processAttempt(testId, attemptDto, user);

        // then
        verify(testService).getTestEntity(testId);
        verify(attemptRepository).save(attemptCaptor.capture());

        AttemptResult attemptResult = attemptCaptor.getValue();
        assertThat(attemptResult.getUser(), is(user));
        assertThat(attemptResult.getTest(), is(test));
        assertThat(attemptResult.getScore(), is(0));
        assertThat(attemptResult.getMaxScore(), is(3));
        assertThat(attemptResult.getAttemptQuestions(), hasSize(2));
        assertThat(attemptResult.getCreatedAt(), is(notNullValue()));

        assertThat(res.getScore(), is(0));
        assertThat(res.getMaxScore(), is(3));
        assertThat(res.getAttemptQuestions(), hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenMoreAnswersThanPossibleNumberOfCorrectAnswers_thenThrowException() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(4L).build(),
                        AttemptAnswerDto.builder().optionId(5L).build() // requires two answers, given 3
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);

        // then
        assertThrows(IllegalStateException.class, () -> attemptService.processAttempt(testId, attemptDto, user));
        verify(testService).getTestEntity(testId);
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenWrongOption_thenThrowException() {
        // given
        String testId = "qwer-1234";

        Test test = buildTest(testId);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptQuestionDto question1 = AttemptQuestionDto.builder()
                .questionId(1L)
                .answers(Set.of(AttemptAnswerDto.builder().optionId(2L).build()))
                .build();

        AttemptQuestionDto question2 = AttemptQuestionDto.builder()
                .questionId(2L)
                .answers(Set.of(
                        AttemptAnswerDto.builder().optionId(3L).build(),
                        AttemptAnswerDto.builder().optionId(6L).build() // wrong option with id 6
                ))
                .build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of(question1, question2)).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);

        // then
        assertThrows(IllegalStateException.class, () -> attemptService.processAttempt(testId, attemptDto, user));
        verify(testService).getTestEntity(testId);
    }

    @org.junit.jupiter.api.Test
    void whenProcessAttempt1_givenTestDoesntExist_thenThrowException() {
        // given
        String testId = "qwer-1234";

        User user = User.builder().id("1234").email("j.doe@mail.com").build();

        AttemptDto attemptDto = AttemptDto.builder().questions(Set.of()).build();

        // when
        when(testService.getTestEntity(testId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> attemptService.processAttempt(testId, attemptDto, user));
        verify(testService).getTestEntity(testId);
    }

    private Test buildTest(String id) {
        Question question1 = Question.builder()
                .id(1L)
                .question("What is the correct answer to question 1?")
                .options(Set.of(
                        Option.builder().id(1L).option("a").build(),
                        Option.builder().id(2L).option("b").correct(true).build()
                ))
                .build();

        Question question2 = Question.builder()
                .id(2L)
                .question("What is the correct answer to question 2?")
                .options(Set.of(
                        Option.builder().id(3L).option("a").build(),
                        Option.builder().id(4L).option("b").correct(true).build(),
                        Option.builder().id(5L).option("c").correct(true).build()
                ))
                .build();

        return Test.builder()
                .id(id)
                .name("Test")
                .questions(Set.of(question1, question2))
                .build();
    }
}