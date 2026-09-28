package com.student.bus_pass_tracker.repository;

import com.student.bus_pass_tracker.entity.ApplicationStatus;
import com.student.bus_pass_tracker.entity.PassApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PassApplicationRepository extends JpaRepository<PassApplication, Long> {

    // Feature 4: all applications of a student, newest first
    List<PassApplication> findByStudentIdOrderByAppliedAtDesc(Long studentId);

    // Rule: one active pass per student.
    // "Active" = PENDING, or APPROVED and not yet expired.
    @Query("""
           select count(a) > 0 from PassApplication a
           where a.student.id = :studentId
             and (a.status = com.student.bus_pass_tracker.entity.ApplicationStatus.PENDING
                  or (a.status = com.student.bus_pass_tracker.entity.ApplicationStatus.APPROVED
                      and a.validUntil >= :today))
           """)
    boolean existsActiveForStudent(@Param("studentId") Long studentId,
                                   @Param("today") LocalDate today);

    // Feature 5: approved passes expiring between today and today + 30 days
    List<PassApplication> findByStatusAndValidUntilBetweenOrderByValidUntilAsc(
            ApplicationStatus status, LocalDate from, LocalDate to);
}