package com.example.testing.payload.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @NotBlank(message = "Question is required")
    @Length(max = 512, message = "Question must be up to 512 characters")
    private String question;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Size(min = 2, message = "You need to provided at least two distinct options")
    private Set<OptionDto> options;
}
