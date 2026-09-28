package com.student.bus_pass_tracker.exception;

import java.time.LocalDateTime;

// One consistent JSON shape for every error the API returns
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {}