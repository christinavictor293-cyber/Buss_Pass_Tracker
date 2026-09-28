package com.student.bus_pass_tracker.service;

import com.student.bus_pass_tracker.dto.ApplyRequest;
import com.student.bus_pass_tracker.dto.Decision;
import com.student.bus_pass_tracker.dto.ReviewRequest;
import com.student.bus_pass_tracker.entity.ApplicationStatus;
import com.student.bus_pass_tracker.entity.BusRoute;
import com.student.bus_pass_tracker.entity.PassApplication;
import com.student.bus_pass_tracker.entity.Student;
import com.student.bus_pass_tracker.exception.BusinessRuleException;
import com.student.bus_pass_tracker.exception.NotFoundException;
import com.student.bus_pass_tracker.repository.BusRouteRepository;
import com.student.bus_pass_tracker.repository.PassApplicationRepository;
import com.student.bus_pass_tracker.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PassApplicationService {

    private static final int DEFAULT_VALIDITY_MONTHS = 6;
    private static final int EXPIRY_WINDOW_DAYS = 30;

    private final PassApplicationRepository applications;
    private final StudentRepository students;
    private final BusRouteRepository routes;

    public PassApplicationService(PassApplicationRepository applications,
                                  StudentRepository students,
                                  BusRouteRepository routes) {
        this.applications = applications;
        this.students = students;
        this.routes = routes;
    }

    // ---------------------------------------------------------------
    // Feature 1: student submits an application
    // ---------------------------------------------------------------
    public PassApplication apply(ApplyRequest req) {
        // 1. Load referenced records (404 if missing)
        Student student = students.findById(req.studentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + req.studentId()));
        BusRoute route = routes.findById(req.routeId())
                .orElseThrow(() -> new NotFoundException("Route not found: " + req.routeId()));

        // 2. Validate every rule BEFORE creating or saving anything
        validateBoardingPoint(route, req.boardingPoint());
        validateNoActivePass(student);

        // 3. Only now build and save
        PassApplication application = new PassApplication();
        application.setStudent(student);
        application.setRoute(route);
        application.setBoardingPoint(req.boardingPoint());
        application.setPhotoRef(req.photoRef());
        application.setStatus(ApplicationStatus.PENDING);
        return applications.save(application);
    }

    // ---------------------------------------------------------------
    // Features 2 and 3: admin approves or rejects
    // ---------------------------------------------------------------
    public PassApplication review(Long applicationId, ReviewRequest req) {
        PassApplication application = applications.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found: " + applicationId));

        // Rule: only a pending application can be reviewed
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessRuleException("Application is already "
                    + application.getStatus() + " and cannot be reviewed again");
        }

        if (req.decision() == Decision.REJECT) {
            reject(application, req);
        } else {
            approve(application, req);
        }

        application.setAdminRemark(req.remark());
        application.setReviewedAt(LocalDateTime.now());
        return applications.save(application);
    }

    private void reject(PassApplication application, ReviewRequest req) {
        // Rule: a rejection must include a reason. Checked BEFORE any field is changed.
        if (req.rejectionReason() == null || req.rejectionReason().isBlank()) {
            throw new BusinessRuleException("A rejection reason is required to reject an application");
        }
        application.setStatus(ApplicationStatus.REJECTED);
        application.setRejectionReason(req.rejectionReason().trim());
    }

    private void approve(PassApplication application, ReviewRequest req) {
        int months = req.validityMonths() == null ? DEFAULT_VALIDITY_MONTHS : req.validityMonths();
        LocalDate from = LocalDate.now();

        application.setStatus(ApplicationStatus.APPROVED);
        application.setValidFrom(from);
        application.setValidUntil(from.plusMonths(months));
        application.setPassNumber(generatePassNumber(application.getId(), from));
    }

    // ---------------------------------------------------------------
    // Feature 4: student views status
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public PassApplication get(Long applicationId) {
        return applications.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found: " + applicationId));
    }

    @Transactional(readOnly = true)
    public List<PassApplication> listForStudent(Long studentId) {
        if (!students.existsById(studentId)) {
            throw new NotFoundException("Student not found: " + studentId);
        }
        return applications.findByStudentIdOrderByAppliedAtDesc(studentId);
    }

    // ---------------------------------------------------------------
    // Feature 5: passes expiring in the next 30 days
    // ---------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<PassApplication> expiringWithin30Days() {
        LocalDate today = LocalDate.now();
        return applications.findByStatusAndValidUntilBetweenOrderByValidUntilAsc(
                ApplicationStatus.APPROVED, today, today.plusDays(EXPIRY_WINDOW_DAYS));
    }

    // ---------------------------------------------------------------
    // Rule helpers
    // ---------------------------------------------------------------

    /** Rule: a student can have only one active pass at a time. */
    private void validateNoActivePass(Student student) {
        if (applications.existsActiveForStudent(student.getId(), LocalDate.now())) {
            throw new BusinessRuleException("Student " + student.getId()
                    + " already has an active pass or a pending application");
        }
    }

    private void validateBoardingPoint(BusRoute route, String boardingPoint) {
        if (!route.getBoardingPoints().contains(boardingPoint)) {
            throw new BusinessRuleException("Boarding point '" + boardingPoint
                    + "' is not on route " + route.getRouteCode());
        }
    }

    private String generatePassNumber(Long applicationId, LocalDate issuedOn) {
        return String.format("BP-%d-%06d", issuedOn.getYear(), applicationId);
    }

     // Student cancels an application. Only PENDING ones can be cancelled.
    public void cancel(Long applicationId) {
        PassApplication application = applications.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found: " + applicationId));

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BusinessRuleException("Only a PENDING application can be cancelled; this one is "
                    + application.getStatus());
        }
        applications.delete(application);
    }
}