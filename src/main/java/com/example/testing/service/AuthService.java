package com.example.testing.service;

import com.example.testing.payload.UserDto;

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
}
