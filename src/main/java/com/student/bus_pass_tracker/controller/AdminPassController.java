package com.student.bus_pass_tracker.controller;

import com.student.bus_pass_tracker.dto.ApplicationResponse;
import com.student.bus_pass_tracker.dto.ReviewRequest;
import com.student.bus_pass_tracker.service.PassApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminPassController {

    private final PassApplicationService applicationService;

    public AdminPassController(PassApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Features 2 and 3: approve or reject with a remark -> 200 OK
    // 404 unknown application, 422 missing rejection reason or already reviewed
    @PutMapping("/applications/{id}/review")
    public ResponseEntity<ApplicationResponse> review(
            @PathVariable @NotNull @Positive Long id,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApplicationResponse.from(applicationService.review(id, request)));
    }

    // Feature 5: approved passes expiring in the next 30 days -> 200 OK
    @GetMapping("/passes/expiring")
    public ResponseEntity<List<ApplicationResponse>> expiring() {
        List<ApplicationResponse> body = applicationService.expiringWithin30Days().stream()
                .map(ApplicationResponse::from).toList();
        return ResponseEntity.ok(body);
    }
}