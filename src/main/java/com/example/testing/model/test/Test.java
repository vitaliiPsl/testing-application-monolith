package com.example.testing.model.test;

import com.example.testing.model.Subject;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tests", indexes = {
        @Index(name = "idx_tests_subject", columnList = "subject_id")
})
public class Test {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    private Subject subject;

    private String name;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private Set<Question> questions = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
