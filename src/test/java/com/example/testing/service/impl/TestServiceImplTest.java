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
import java.util.Optional;
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

    // UPDATE
    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenValidRequest_thenUpdateTest() {
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

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).subject(subject).name("First test").build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.of(test));
        when(testRepository.save(any(Test.class))).then(AdditionalAnswers.returnsFirstArg());

        TestDto res = testService.updateTest(subjectId, testId, testDto, user);

        // then
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
        verify(testRepository).save(testCaptor.capture());

        assertThat(res.getName(), is(testDto.getName()));

        Test capturedTest = testCaptor.getValue();
        assertThat(capturedTest.getId(), is(testId));
        assertThat(capturedTest.getName(), is(testDto.getName()));
        assertThat(capturedTest.getSubject(), is(subject));
        assertThat(capturedTest.getUpdatedAt(), is(notNullValue()));

        Set<Question> questions = capturedTest.getQuestions();
        assertThat(questions, hasSize(2));
        assertThat(List.copyOf(questions).get(0).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(0).getCorrectOptions(), hasSize(1));
        assertThat(List.copyOf(questions).get(1).getOptions(), hasSize(2));
        assertThat(List.copyOf(questions).get(1).getCorrectOptions(), hasSize(1));
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenInvalidNumberOfCorrectOptions_thenThrowException() {
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

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).subject(subject).name("First test").build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.of(test));

        // then
        assertThrows(IllegalStateException.class, () -> testService.updateTest(subjectId, testId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenInvalidNumberOptions_thenThrowException() {
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

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).subject(subject).name("First test").build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.of(test));

        // then
        assertThrows(IllegalStateException.class, () -> testService.updateTest(subjectId, testId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenTestDoesnt_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        TestDto testDto = TestDto.builder()
                .name("Test")
                .build();

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.updateTest(subjectId, testId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";
        TestDto testDto = TestDto.builder()
                .name("Test")
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.updateTest(subjectId, testId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

    @org.junit.jupiter.api.Test
    void whenUpdateTest_givenUserIsNotEducatorOfGivenSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";
        TestDto testDto = TestDto.builder()
                .name("Test")
                .build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ForbiddenException.class);

        // then
        assertThrows(ForbiddenException.class, () -> testService.updateTest(subjectId, testId, testDto, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

    // DELETE
    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenValidRequest_thenDeleteTest() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).subject(subject).name("First test").build();

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.of(test));

        testService.deleteTest(subjectId, testId, user);

        // then
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
        verify(testRepository).save(testCaptor.capture());

        Test capturedTest = testCaptor.getValue();
        assertThat(capturedTest.getId(), is(testId));
        assertThat(capturedTest.getDeletedAt(), is(notNullValue()));
    }

    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenTestDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();
        Subject subject = Subject.builder().id(subjectId).educator(user).build();

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.deleteTest(subjectId, testId, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
    }

    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.deleteTest(subjectId, testId, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

    @org.junit.jupiter.api.Test
    void whenDeleteTest_givenUserIsNotEducatorOfTheSubject_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        User user = User.builder().id("qwer-1234").email("j.doe@mail.com").role(UserRole.EDUCATOR).build();

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntityAndVerifyEducator(subjectId, user)).thenThrow(ForbiddenException.class);

        // then
        assertThrows(ForbiddenException.class, () -> testService.deleteTest(subjectId, testId, user));
        verify(subjectService).getSubjectEntityAndVerifyEducator(subjectId, user);
    }

    // FETCH
    @org.junit.jupiter.api.Test
    void whenGetTestById_givenTestExist_thenReturnTest() {
        // given
        String subjectId = "1234-qwer";

        Subject subject = Subject.builder().id(subjectId).build();

        String testId = "qwer-1234";
        Test test = Test.builder().id(testId).subject(subject).name("First test").build();

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.of(test));

        TestDto res = testService.getTestById(subjectId, testId);

        // then
        verify(subjectService).getSubjectEntity(subjectId);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);

        assertThat(res.getId(), is(test.getId()));
        assertThat(res.getName(), is(test.getName()));
    }

    @org.junit.jupiter.api.Test
    void whenGetTestById_givenTestDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        Subject subject = Subject.builder().id(subjectId).build();

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenReturn(subject);
        when(testRepository.findByIdAndSubjectAndDeletedAtIsNull(testId, subject)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.getTestById(subjectId, testId));
        verify(subjectService).getSubjectEntity(subjectId);
        verify(testRepository).findByIdAndSubjectAndDeletedAtIsNull(testId, subject);
    }

    @org.junit.jupiter.api.Test
    void whenGetTestById_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        String testId = "qwer-1234";

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.getTestById(subjectId, testId));
        verify(subjectService).getSubjectEntity(subjectId);
    }

    @org.junit.jupiter.api.Test
    void whenGetTestsBySubjectId_givenTestsWithGivenSubjectIdExist_thenReturnTests() {
        // given
        String subjectId = "1234-qwer";

        Subject subject = Subject.builder().id(subjectId).build();

        List<Test> tests = List.of(
                Test.builder().id("1234").subject(subject).name("First test").build(),
                Test.builder().id("4321").subject(subject).name("Second test").build()
        );

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenReturn(subject);
        when(testRepository.findBySubjectAndDeletedAtIsNull(subject)).thenReturn(tests);

        List<TestDto> res = testService.getTestsBySubjectId(subjectId);

        // then
        verify(subjectService).getSubjectEntity(subjectId);
        verify(testRepository).findBySubjectAndDeletedAtIsNull(subject);

        assertThat(res, hasSize(2));
    }

    @org.junit.jupiter.api.Test
    void whenGetTestsBySubjectId_givenTestsWithGivenSubjectIdDoesntExist_thenReturnEmptyList() {
        // given
        String subjectId = "1234-qwer";

        Subject subject = Subject.builder().id(subjectId).build();

        List<Test> tests = List.of();

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenReturn(subject);
        when(testRepository.findBySubjectAndDeletedAtIsNull(subject)).thenReturn(tests);

        List<TestDto> res = testService.getTestsBySubjectId(subjectId);

        // then
        verify(subjectService).getSubjectEntity(subjectId);
        verify(testRepository).findBySubjectAndDeletedAtIsNull(subject);

        assertThat(res, hasSize(0));
    }

    @org.junit.jupiter.api.Test
    void whenGetTestsBySubjectId_givenSubjectDoesntExist_thenThrowException() {
        // given
        String subjectId = "1234-qwer";

        // when
        when(subjectService.getSubjectEntity(subjectId)).thenThrow(ResourceNotFoundException.class);

        // then
        assertThrows(ResourceNotFoundException.class, () -> testService.getTestsBySubjectId(subjectId));
        verify(subjectService).getSubjectEntity(subjectId);
    }
}