package com.student.bus_pass_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record StudentRequest(
        @NotBlank String name,
        @NotBlank String rollNumber,
        @NotBlank @Email String email,
        String department
) {}
