package com.example.testing.payload.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @NotBlank(message = "Option is required")
    @Length(max = 512, message = "Option must be up to 512 characters")
    private String option;

    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private boolean correct;
}
