package com.example.testing.service.impl;

import com.example.testing.exceptions.ResourceAlreadyExistException;
import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.User;
import com.example.testing.payload.UserDto;
import com.example.testing.payload.auth.SignInRequestDto;
import com.example.testing.payload.auth.SignInResponseDto;
import com.example.testing.repository.UserRepository;
import com.example.testing.service.AuthService;
import com.example.testing.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final ModelMapper mapper;

    @Override
    public UserDto signUp(UserDto userDto) {
        log.debug("Register new user: {}", userDto);

        // check if email is available
        Optional<User> existing = userRepository.findByEmail(userDto.getEmail());
        if (existing.isPresent()) {
            log.error("Email '{}' is already taken", userDto.getEmail());
            throw new ResourceAlreadyExistException("User", "email", userDto.getEmail());
        }

        // save user
        User user = createUser(userDto);
        userRepository.save(user);

        return mapUserToUserDto(user);
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto request) {
        log.debug("Authenticate user: {}", request.getEmail());

        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        authentication = authManager.authenticate(authentication);

        // authentication principal
        User user = (User) authentication.getPrincipal();

        String token = jwtService.createToken(user);
        return new SignInResponseDto(token);
    }

    @Override
    public Authentication exchangeToken(String token) {
        log.debug("Exchange token");

        String userId = jwtService.decodeToken(token);

        User user = getUser(userId);
        return new PreAuthenticatedAuthenticationToken(user, token, user.getAuthorities());
    }

    private User getUser(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            log.error("User with id '{}' doesn't exist", userId);
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return optionalUser.get();
    }

    private User createUser(UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(encodedPassword)
                .role(userDto.getRole())
                .enabled(true)
                .build();
    }

    private UserDto mapUserToUserDto(User user) {
        return mapper.map(user, UserDto.class);
    }
}
