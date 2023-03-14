package com.example.testing.service;

import com.example.testing.payload.UserDto;
import com.example.testing.payload.auth.SignInRequestDto;
import com.example.testing.payload.auth.SignInResponseDto;

/**
 * Authentication service
 */
public interface AuthService {

    /**
     * Register a new user
     *
     * @param userDto user to register
     * @return registered user
     */
    UserDto signUp(UserDto userDto);

    /**
     * Authenticate the user with given credentials
     *
     * @param request credentials of the user
     * @return sign in response that contains the JWT token
     */
    SignInResponseDto signIn(SignInRequestDto request);
}
