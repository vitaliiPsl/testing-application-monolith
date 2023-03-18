package com.example.testing.payload.attempt;

import com.example.testing.payload.UserDto;
import com.example.testing.payload.test.TestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private TestDto test;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Set<AttemptQuestionDto> attemptQuestions = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer score;

    private Integer maxScore;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}
