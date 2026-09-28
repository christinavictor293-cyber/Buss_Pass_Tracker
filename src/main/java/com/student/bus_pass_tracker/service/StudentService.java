package com.student.bus_pass_tracker.service;

import com.student.bus_pass_tracker.dto.StudentRequest;
import com.student.bus_pass_tracker.entity.Student;
import com.student.bus_pass_tracker.exception.BusinessRuleException;
import com.student.bus_pass_tracker.exception.NotFoundException;
import com.student.bus_pass_tracker.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository students;

    public StudentService(StudentRepository students) {
        this.students = students;
    }

    public Student createStudent(StudentRequest req) {
        // Rule: email and roll number must be unique. Checked before saving.
        if (students.existsByEmailOrRollNumber(req.email(), req.rollNumber())) {
            throw new BusinessRuleException("A student with this email or roll number already exists");
        }
        Student student = new Student();
        student.setName(req.name());
        student.setRollNumber(req.rollNumber());
        student.setEmail(req.email());
        student.setDepartment(req.department());
        return students.save(student);
    }

    @Transactional(readOnly = true)
    public Student getStudent(Long id) {
        return students.findById(id)
                .orElseThrow(() -> new NotFoundException("Student not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Student> listStudents() {
        return students.findAll();
    }
}