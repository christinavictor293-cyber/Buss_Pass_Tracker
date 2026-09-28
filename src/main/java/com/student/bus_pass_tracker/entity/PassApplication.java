package com.student.bus_pass_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class PassApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many applications -> one student (owns the student_id foreign key)
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Many applications -> one route (owns the route_id foreign key)
    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private BusRoute route;

    @NotBlank
    @Column(nullable = false)
    private String boardingPoint;

    @NotBlank
    @Column(nullable = false)
    private String photoRef;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // Admin's comment on approve or reject
    private String adminRemark;

    // Mandatory when status = REJECTED (enforced in PassApplicationService)
    private String rejectionReason;

    // Set only on approval
    @Column(unique = true)
    private String passNumber;
    private LocalDate validFrom;
    private LocalDate validUntil;

    private LocalDateTime appliedAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;

    public PassApplication() {}

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }

    public void setStudent(Student student) { this.student = student; }

    public BusRoute getRoute() { return route; }

    public void setRoute(BusRoute route) { this.route = route; }

    public String getBoardingPoint() { return boardingPoint; }

    public void setBoardingPoint(String boardingPoint) { this.boardingPoint = boardingPoint; }

    public String getPhotoRef() { return photoRef; }

    public void setPhotoRef(String photoRef) { this.photoRef = photoRef; }

    public ApplicationStatus getStatus() { return status; }

    public void setStatus(ApplicationStatus status) { this.status = status; }

    public String getAdminRemark() { return adminRemark; }

    public void setAdminRemark(String adminRemark) { this.adminRemark = adminRemark; }

    public String getRejectionReason() { return rejectionReason; }

    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getPassNumber() { return passNumber; }

    public void setPassNumber(String passNumber) { this.passNumber = passNumber; }

    public LocalDate getValidFrom() { return validFrom; }

    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }

    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public LocalDateTime getAppliedAt() { return appliedAt; }

    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }

    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}