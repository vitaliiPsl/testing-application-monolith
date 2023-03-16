package com.example.testing.payload.test;

import com.example.testing.model.Subject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Subject subject;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @NotBlank(message = "Test name is required")
    @Length(max = 255, message = "Test name must be up to 512 characters")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Size(min = 2, message = "Test must contain at least two distinct questions")
    private Set<QuestionDto> questions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
