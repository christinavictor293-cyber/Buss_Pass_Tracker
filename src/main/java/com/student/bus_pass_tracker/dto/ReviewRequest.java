package com.student.bus_pass_tracker.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Decision decision,
        String remark,
        String rejectionReason,
        Integer validityMonths
) {}
