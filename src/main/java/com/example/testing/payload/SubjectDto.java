package com.example.testing.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto educator;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @NotBlank(message = "Name of the subject is required")
    @Length(max = 1024, message = "The name must be up to 255 characters")
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    @Length(max = 1024, message = "The description must be up to 1024 characters")
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}
