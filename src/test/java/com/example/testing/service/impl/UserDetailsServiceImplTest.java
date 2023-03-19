package com.example.testing.service.impl;

import com.example.testing.model.User;
import com.example.testing.repository.UserRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Test
    void givenLoadUserByUser_whenUserExist_thenReturnUser() {
        // given
        String email = "j.doe@mail.com";
        User user = User.builder().email(email).build();

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails res = userDetailsService.loadUserByUsername(email);

        // then
        verify(userRepository).findByEmail(email);
        assertThat(res.getUsername(), Matchers.is(email));
    }

    @Test
    void givenLoadUserByUser_whenUserDoesntExist_thenThrowException() {
        // given
        String email = "j.doe@mail.com";

        // when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(email));
        verify(userRepository).findByEmail(email);
    }
}