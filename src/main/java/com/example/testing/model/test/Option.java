package com.example.testing.model.test;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "options", indexes = {
        @Index(name = "idx_options_question", columnList = "question_id")
})
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    private Question question;

    @Column(length = 512)
    private String option;

    @EqualsAndHashCode.Exclude
    private boolean correct;
}
