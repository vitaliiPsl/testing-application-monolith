package com.example.testing.service.impl;

import com.example.testing.exceptions.ForbiddenException;
import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.Subject;
import com.example.testing.model.User;
import com.example.testing.model.UserRole;
import com.example.testing.model.test.Question;
import com.example.testing.model.test.Test;
import com.example.testing.payload.test.OptionDto;
import com.example.testing.payload.test.QuestionDto;
import com.example.testing.payload.test.TestDto;
import com.example.testing.repository.TestRepository;
import com.example.testing.service.SubjectService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    TestRepository testRepository;
    @Mock
    SubjectService subjectService;
    @Spy
    ModelMapper mapper;

    @InjectMocks
    TestServiceImpl testService;

    @Captor
    ArgumentCaptor<Test> testCaptor;

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenValidRequest_thenSaveTest() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is correct answer for question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is correct answer for question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.save(any(Test.class))).then(AdditionalAnswers.returnsFirstArg());

        TestDto res = testService.saveTest(subjectId, testDto, user);

        // then
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).save(testCaptor.capture());

        assertThat(res.getName(), is(testDto.getName()));

        Test test = testCaptor.getValue();
        assertThat(test.getName(), is(testDto.getName()));
        assertThat(test.getSubject(), is(subject));
        assertThat(test.getCreatedAt(), is(notNullValue()));

        Set<Question> questions = test.getQuestions();
        assertThat(questions, hasSize(2));
        assertThat(List.copyOf(questions).get(0).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(0).getCorrectOptions(), hasSize(1));
        assertThat(List.copyOf(questions).get(1).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(1).getCorrectOptions(), hasSize(1));
    }

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenInvalidNumberOfCorrectOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is correct answer for question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").correct(true).build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is correct answer for question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);

        // then
        assertThrows(IllegalStateException.class, () -> testService.saveTest(subjectId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }


    @org.junit.jupiter.api.Test
    void whenSaveTest_givenInvalidNumberOptions_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        QuestionDto question1 = QuestionDto.builder()
                .question("What is correct answer for question 1?")
                .options(Set.of(
                        OptionDto.builder().option("a").build()
                ))
                .build();

        QuestionDto question2 = QuestionDto.builder()
                .question("What is correct answer for question 2?")
                .options(Set.of(
                        OptionDto.builder().option("a").build(),
                        OptionDto.builder().option("b").build()
                ))
                .build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .questions(Set.of(question1, question2))
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);

        // then
        assertThrows(IllegalStateException.class, () -> testService.saveTest(subjectId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

    @org.junit.jupiter.api.Test
    void whenSaveTest_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.saveTest(subjectId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }


    @org.junit.jupiter.api.Test
    void whenSaveTest_givenUserIsNotEducatorOfGivenSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ForbiddenException.class);

        // then
        assertThrows(ForbiddenException.class, () -> testService.saveTest(subjectId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

}