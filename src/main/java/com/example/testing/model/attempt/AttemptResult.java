package com.example.testing.model.attempt;

import com.example.testing.model.User;
import com.example.testing.model.test.Test;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attempt")
public class AttemptResult {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Test test;

    @EqualsAndHashCode.Exclude
    @OneToMany(cascade = CascadeType.ALL)
    private Set<AttemptQuestion> attemptQuestions;

    private Integer score;
    private Integer maxScore;

    private LocalDateTime createdAt;
}
