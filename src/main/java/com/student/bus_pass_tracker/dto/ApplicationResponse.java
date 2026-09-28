package com.student.bus_pass_tracker.dto;

import com.student.bus_pass_tracker.entity.ApplicationStatus;
import com.student.bus_pass_tracker.entity.PassApplication;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApplicationResponse(
        Long id,
        Long studentId,
        Long routeId,
        String boardingPoint,
        ApplicationStatus status,
        String adminRemark,
        String rejectionReason,
        String passNumber,
        LocalDate validFrom,
        LocalDate validUntil,
        LocalDateTime appliedAt,
        LocalDateTime reviewedAt
) {
    public static ApplicationResponse from(PassApplication a) {
        return new ApplicationResponse(
                a.getId(),
                a.getStudent().getId(),
                a.getRoute().getId(),
                a.getBoardingPoint(),
                a.getStatus(),
                a.getAdminRemark(),
                a.getRejectionReason(),
                a.getPassNumber(),
                a.getValidFrom(),
                a.getValidUntil(),
                a.getAppliedAt(),
                a.getReviewedAt()
        );
    }
}
