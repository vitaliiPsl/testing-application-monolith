package com.example.testing.controller;

import com.example.testing.payload.UserDto;
import com.example.testing.payload.auth.SignInRequestDto;
import com.example.testing.payload.auth.SignInResponseDto;
import com.example.testing.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("signup")
    UserDto signUp(@RequestBody @Valid UserDto req) {
        return authService.signUp(req);
    }

    @PostMapping("signin")
    SignInResponseDto signUp(@RequestBody @Valid SignInRequestDto req) {
        return authService.signIn(req);
    }
}
