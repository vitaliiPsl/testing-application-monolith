package com.example.testing.payload.attempt;

import com.example.testing.payload.test.QuestionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptQuestionDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long questionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private QuestionDto question;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Set<AttemptAnswerDto> answers = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer score;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer maxScore;
}
