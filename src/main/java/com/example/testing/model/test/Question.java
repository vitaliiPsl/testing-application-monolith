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
@Table(name = "questions", indexes = {
        @Index(name = "idx_questions_test", columnList = "test_id")
})
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    private Test test;

    @Column(length = 512)
    private String question;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private Set<Option> options = new HashSet<>();

    public Set<Option> getCorrectOptions() {
        return options.stream().filter(Option::isCorrect).collect(Collectors.toSet());
    }
}
