package com.example.testing.payload.attempt;

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
public class AttemptDto {

    private Set<AttemptQuestionDto> questions = new HashSet<>();
}
