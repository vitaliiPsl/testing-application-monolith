package com.example.testing.model;

import com.example.testing.model.test.Test;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private User educator;

    private String name;

    @Column(length = 1024)
    private String description;

    @EqualsAndHashCode.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "subject", cascade = CascadeType.REMOVE)
    private Set<Test> tests = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
