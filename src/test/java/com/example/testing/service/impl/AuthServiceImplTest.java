package com.example.testing.service.impl;

import com.example.testing.exceptions.ResourceAlreadyExistException;
import com.example.testing.model.User;
import com.example.testing.model.UserRole;
import com.example.testing.payload.UserDto;
import com.example.testing.payload.auth.SignInRequestDto;
import com.example.testing.payload.auth.SignInResponseDto;
import com.example.testing.repository.UserRepository;
import com.example.testing.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authManager;

    @Spy
    ModelMapper mapper;

    @InjectMocks
    AuthServiceImpl authService;


    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    void whenSignUp_givenRegistrationDataIsValid_thenCreateNewUser() {
        // given
        UserDto userDto = UserDto.builder()
                .email("j.doe@mail.com")
                .password("password")
                .build();

        String encodedPassword = "rkep4h1etq8i";

        // when
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(Mockito.any(User.class))).then(AdditionalAnswers.returnsFirstArg());

        UserDto response = authService.signUp(userDto);

        // then
        verify(userRepository).findByEmail(userDto.getEmail());
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRepository).save(userCaptor.capture());

        User user = userCaptor.getValue();
        assertThat(user.getFirstName(), is(user.getFirstName()));
        assertThat(user.getLastName(), is(user.getLastName()));
        assertThat(user.getEmail(), is(userDto.getEmail()));
        assertThat(user.getPassword(), is(encodedPassword));
        assertThat(user.getRole(), is(userDto.getRole()));
        assertThat(user.isEnabled(), is(true));

        assertThat(response.getEmail(), is(userDto.getEmail()));
    }

    @Test
    void whenSignUp_givenEmailIsTaken_thenThrowException() {
        // given
        UserDto userDto = UserDto.builder().email("j.doe@mail.com").build();

        User otherUser = User.builder().email(userDto.getEmail()).build();

        // when
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(otherUser));

        // then
        assertThrows(ResourceAlreadyExistException.class, () -> authService.signUp(userDto));
        verify(userRepository).findByEmail(userDto.getEmail());
    }

    @Test
    void whenSignIn_givenCredentialsAreValid_thenGenerateJwt() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";
        String jwt = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";

        SignInRequestDto request = SignInRequestDto.builder()
                .email(email)
                .password(password)
                .build();


        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        User user = User.builder().id("1234").email("j.doe@mail.com").build();
        Authentication verified = new PreAuthenticatedAuthenticationToken(user, "");

        // when
        when(authManager.authenticate(auth)).thenReturn(verified);
        when(jwtService.createToken(user)).thenReturn(jwt);

        SignInResponseDto response = authService.signIn(request);

        // then
        verify(authManager).authenticate(auth);
        verify(jwtService).createToken(user);

        assertThat(response.getToken(), is(jwt));
    }

    @Test
    void whenSignIn_givenCredentialsAreInvalid_thenThrowException() {
        // given
        String email = "j.doe@mail.com";
        String password = "password";

        SignInRequestDto request = SignInRequestDto.builder().email(email).password(password).build();

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        // when
        when(authManager.authenticate(auth)).thenThrow(new BadCredentialsException("Invalid password"));

        // then
        assertThrows(BadCredentialsException.class, () -> authService.signIn(request));
        verify(authManager).authenticate(auth);
    }

    @Test
    void whenExchangeToken_givenTokenIsValidAndUserExistAndEnabled_thenReturnUserAuthentication() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";

        String userId = "1234";
        User user = User.builder()
                .id(userId)
                .email("j.doe@mail.com")
                .role(UserRole.STUDENT)
                .enabled(true).build();

        // when
        when(jwtService.decodeToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Authentication res = authService.exchangeToken(token);

        // then
        verify(jwtService).decodeToken(token);
        verify(userRepository).findById(userId);

        assertThat(res.isAuthenticated(), is(true));
        assertThat((User) res.getPrincipal(), is(user));
        assertThat((String) res.getCredentials(), is(token));
    }

    @Test
    void whenExchangeToken_givenUserDoesntExist_thenThrowException() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";

        String userId = "1234";

        // when
        when(jwtService.decodeToken(token)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // then
        assertThrows(RuntimeException.class, () -> authService.exchangeToken(token));
        verify(jwtService).decodeToken(token);
        verify(userRepository).findById(userId);
    }

    @Test
    void whenExchangeToken_givenTokenIsInvalid_thenThrowException() {
        // given
        String token = "eyJ0eXA.eyJzdWIi.Ou-2-0gYTg";

        // when
        when(jwtService.decodeToken(token)).thenThrow(new RuntimeException());

        // then
        assertThrows(RuntimeException.class, () -> authService.exchangeToken(token));
        verify(jwtService).decodeToken(token);
    }
}