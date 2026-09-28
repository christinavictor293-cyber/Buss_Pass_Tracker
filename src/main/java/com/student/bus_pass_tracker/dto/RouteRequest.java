package com.student.bus_pass_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RouteRequest(
        @NotBlank String routeCode,
        @NotBlank String routeName,
        @NotBlank String startPoint,
        @NotBlank String endPoint,
        @NotEmpty List<String> boardingPoints
) {}
