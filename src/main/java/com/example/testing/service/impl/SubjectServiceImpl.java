package com.example.testing.service.impl;

import com.example.testing.exceptions.ForbiddenException;
import com.example.testing.exceptions.ResourceNotFoundException;
import com.example.testing.model.Subject;
import com.example.testing.model.User;
import com.example.testing.model.UserRole;
import com.example.testing.payload.SubjectDto;
import com.example.testing.repository.SubjectRepository;
import com.example.testing.repository.UserRepository;
import com.example.testing.service.SubjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public SubjectDto saveSubject(SubjectDto req, User user) {
        log.debug("Save subject: {}", req);

        if (user.getRole() != UserRole.EDUCATOR) {
            log.error("Only educator can create subjects");
            throw new ForbiddenException("Only educator can create subjects");
        }

        // save subject
        Subject subject = createSubject(req, user);
        subject = subjectRepository.save(subject);

        return mapSubjectToSubjectDto(subject);
    }

    @Override
    public SubjectDto updateSubject(String subjectId, SubjectDto req, User user) {
        log.debug("Update subject with id {}. New data: {}", subjectId, req);

        // find subject by id
        Subject subject = getSubject(subjectId);

        // check that user is the subject educator
        if (!subject.getEducator().equals(user)) {
            log.error("User is not the educator on given subject");
            throw new ForbiddenException("Not a subject educator");
        }

        // update properties
        subject.setName(req.getName());
        subject.setDescription(req.getDescription());
        subject.setUpdatedAt(LocalDateTime.now());

        subject = subjectRepository.save(subject);

        return mapSubjectToSubjectDto(subject);
    }

    @Override
    public void deleteSubject(String subjectId, User user) {
        log.debug("Delete subject with id {}", subjectId);

        // find subject by id
        Subject subject = getSubject(subjectId);

        // check that user is the subject educator
        if (!subject.getEducator().equals(user)) {
            log.error("User is not the educator on given subject");
            throw new ForbiddenException("Not a subject educator");
        }

        // soft deletion
        subject.setDeletedAt(LocalDateTime.now());

        subjectRepository.save(subject);
    }

    @Override
    public SubjectDto getSubjectById(String subjectId) {
        log.debug("Get subject with id {}", subjectId);

        // find subject by id
        Subject subject = getSubject(subjectId);

        return mapSubjectToSubjectDto(subject);
    }

    @Override
    public List<SubjectDto> getAllSubjects() {
        log.debug("Get all subjects");

        return subjectRepository.findAllByDeletedAtIsNull()
                .stream().map(this::mapSubjectToSubjectDto).collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> getSubjectsByEducatorId(String educatorId) {
        log.debug("Get subject by educator id {}", educatorId);

        User educator = userRepository.findByIdAndRole(educatorId, UserRole.EDUCATOR)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", educatorId));

        return subjectRepository.findAllByEducatorAndDeletedAtIsNull(educator)
                .stream().map(this::mapSubjectToSubjectDto).collect(Collectors.toList());
    }

    @Override
    public Subject getSubjectEntity(String subjectId) {
        log.debug("Get subject with given id {}", subjectId);

        return subjectRepository.findByIdAndDeletedAtIsNull(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
    }

    @Override
    public Subject getSubjectEntityAndVerifyEducator(String subjectId, User user) {
        log.debug("Get subject with given id {} and verify that user is educator of given subject", subjectId);

        Subject subject = getSubjectEntity(subjectId);

        // check if user is an educator of the given subject
        if (!subject.getEducator().equals(user)) {
            log.error("User {} is not an educator on the given subject", user.getId());
            throw new ForbiddenException("Not an educator of the given subject");
        }

        return subject;
    }

    private Subject getSubject(String subjectId) {
        return subjectRepository.findByIdAndDeletedAtIsNull(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
    }

    private Subject createSubject(SubjectDto dto, User educator) {
        return Subject.builder()
                .educator(educator)
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private SubjectDto mapSubjectToSubjectDto(Subject subject) {
        return mapper.map(subject, SubjectDto.class);
    }
}
