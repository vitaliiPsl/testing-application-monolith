package com.example.testing.service.impl;

import com.example.testing.exceptions.ForbiddenException;
import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.Subject;
import com.example.testing.model.User;
import com.example.testing.model.UserRole;
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

import java.util.List;
import java.util.Optional;
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
    void whenProcessAttempt_givenAllAnswersCorrect_thenSaveAttemptResultWithMaxScore() {
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
    void whenProcessAttempt_givenOnlyOneCorrectAnswerToQuestionWithTwoCorrectAnswers_thenSaveAttemptResultWithScore2OutOf3() {
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
    void whenProcessAttempt_givenAllAnswersWrong_thenSaveAttemptResultWithZeroScore() {
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
    void whenProcessAttempt_givenAnswers_thenSaveAttemptResultWithZeroScore() {
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
    void whenProcessAttempt_givenMoreAnswersThanPossibleNumberOfCorrectAnswers_thenThrowException() {
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
    void whenProcessAttempt_givenWrongOption_thenThrowException() {
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
    void whenProcessAttempt_givenTestDoesntExist_thenThrowException() {
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

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptExistAndStudentIsTheOneWhoTookTheTest_thenReturnAttempt() {
        // given
        String attemptId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.STUDENT).build();

        AttemptResult attemptResult = AttemptResult.builder()
                .id(attemptId)
                .user(user)
                .score(10).build();

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attemptResult));
        AttemptResultDto res = attemptService.getAttemptById(attemptId, user);

        // then
        verify(attemptRepository).findById(attemptId);
        assertThat(res.getId(), is(attemptId));
        assertThat(res.getUser().getId(), is(user.getId()));
        assertThat(res.getScore(), is(attemptResult.getScore()));
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptExistAndEducatorIsTeachingTestSubject_thenReturnAttempt() {
        // given
        String attemptId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject subject = Subject.builder().id("1234-5678").educator(user).build();
        Test test = Test.builder().id("5678-1234").subject(subject).build();

        AttemptResult attemptResult = AttemptResult.builder()
                .id(attemptId)
                .test(test)
                .user(user)
                .score(10).build();

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attemptResult));
        AttemptResultDto res = attemptService.getAttemptById(attemptId, user);

        // then
        verify(attemptRepository).findById(attemptId);
        assertThat(res.getId(), is(attemptId));
        assertThat(res.getUser().getId(), is(user.getId()));
        assertThat(res.getScore(), is(attemptResult.getScore()));
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptExistAndStudentIsNotTheOneWhoTookTheTest_thenReturnAttempt() {
        // given
        String attemptId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.STUDENT).build();
        User other = User.builder().id("1234-qwer").email("jane.doe@mail.com").role(UserRole.STUDENT).build();

        Subject subject = Subject.builder().id("1234-5678").educator(other).build();
        Test test = Test.builder().id("5678-1234").subject(subject).build();

        AttemptResult attemptResult = AttemptResult.builder()
                .id(attemptId)
                .test(test)
                .user(other)
                .score(10).build();

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attemptResult));

        // then
        assertThrows(ForbiddenException.class, () -> attemptService.getAttemptById(attemptId, user));
        verify(attemptRepository).findById(attemptId);
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptById_givenAttemptExistAndEducatorIsNotTeachingTestSubject_thenReturnAttempt() {
        // given
        String attemptId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        User other = User.builder().id("1234-qwer").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject subject = Subject.builder().id("1234-5678").educator(other).build();
        Test test = Test.builder().id("5678-1234").subject(subject).build();

        AttemptResult attemptResult = AttemptResult.builder()
                .id(attemptId)
                .test(test)
                .user(other)
                .score(10).build();

        // when
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attemptResult));

        // then
        assertThrows(ForbiddenException.class, () -> attemptService.getAttemptById(attemptId, user));
        verify(attemptRepository).findById(attemptId);
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptByTestId_givenUserIsTheEducatorOfGivenTestSubject_thenReturnAttempts() {
        // given
        String testId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject subject = Subject.builder().id("1234-5678").educator(user).build();
        Test test = Test.builder().id(testId).subject(subject).build();

        List<AttemptResult> attempts = List.of(
                AttemptResult.builder().id("1234").test(test).score(10).build(),
                AttemptResult.builder().id("4321").test(test).score(10).build()
        );

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);
        when(attemptRepository.findByTest(test)).thenReturn(attempts);

        List<AttemptResultDto> res = attemptService.getAttemptsByTestId(testId, user);

        // then
        verify(testService).getTestEntity(testId);
        verify(attemptRepository).findByTest(test);
        assertThat(res, hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptByTestId_givenUserIsNotTheEducatorOfGivenTestSubject_thenThrowException() {
        // given
        String testId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        User other = User.builder().id("1234-qwer").email("jane.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject subject = Subject.builder().id("1234-5678").educator(other).build();
        Test test = Test.builder().id(testId).subject(subject).build();

        // when
        when(testService.getTestEntity(testId)).thenReturn(test);

        // then
        assertThrows(ForbiddenException.class, () -> attemptService.getAttemptsByTestId(testId, user));
        verify(testService).getTestEntity(testId);
    }

    @org.junit.jupiter.api.Test
    void whenGetAttemptByTestId_givenTestDoesntExist_thenThrowException() {
        // given
        String testId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        // when
        when(testService.getTestEntity(testId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> attemptService.getAttemptsByTestId(testId, user));
        verify(testService).getTestEntity(testId);
    }


    @org.junit.jupiter.api.Test
    void whenGetAttemptByUser_thenReturnAttemptsOfGivenUser() {
        // given
        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        Subject subject = Subject.builder().id("1234-5678").educator(user).build();
        Test test = Test.builder().subject(subject).build();

        List<AttemptResult> attempts = List.of(
                AttemptResult.builder().id("1234").test(test).score(10).build(),
                AttemptResult.builder().id("4321").test(test).score(10).build()
        );

        // when
        when(attemptRepository.findByUser(user)).thenReturn(attempts);

        List<AttemptResultDto> res = attemptService.getAttemptsByUser(user);

        // then
        verify(attemptRepository).findByUser(user);
        assertThat(res, hasSize(2));
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