package com.example.testing.controller;

import com.example.testing.payload.UserDto;
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
    UserDto signUp(@RequestBody @Valid UserDto userDto) {
        return authService.signUp(userDto);
    }
}
