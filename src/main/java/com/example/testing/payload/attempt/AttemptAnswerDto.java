package com.example.testing.payload.attempt;

import com.example.testing.payload.test.OptionDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long optionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private OptionDto option;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean correct;
}
