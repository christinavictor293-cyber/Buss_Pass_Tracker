package com.student.bus_pass_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApplyRequest(
        @NotNull @Positive Long studentId,
        @NotNull @Positive Long routeId,
        @NotBlank String boardingPoint,
        @NotBlank String photoRef
) {}
