package com.student.bus_pass_tracker.repository;

import com.student.bus_pass_tracker.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Used when registering a student, to block duplicates
    boolean existsByEmailOrRollNumber(String email, String rollNumber);
}