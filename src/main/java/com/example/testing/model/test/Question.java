package com.example.testing.model.test;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String question;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Option> options = new HashSet<>();

    public Set<Option> getCorrectOptions() {
        return options.stream().filter(Option::isCorrect).collect(Collectors.toSet());
    }
}
