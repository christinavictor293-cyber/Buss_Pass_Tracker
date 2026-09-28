package com.student.bus_pass_tracker.controller;

import com.student.bus_pass_tracker.dto.ApplicationResponse;
import com.student.bus_pass_tracker.dto.ApplyRequest;
import com.student.bus_pass_tracker.entity.PassApplication;
import com.student.bus_pass_tracker.service.PassApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class PassApplicationController {

    private final PassApplicationService applicationService;

    public PassApplicationController(PassApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // Feature 1: submit application -> 201 Created + Location
    // 404 unknown student/route, 422 active pass exists or bad boarding point
    @PostMapping("/applications")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplyRequest request) {
        PassApplication saved = applicationService.apply(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(ApplicationResponse.from(saved));
    }

    // Feature 4: check status of one application -> 200, or 404
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationResponse> getStatus(@PathVariable @NotNull @Positive Long id) {
        return ResponseEntity.ok(ApplicationResponse.from(applicationService.get(id)));
    }

    // Feature 4: all applications of a student -> 200, or 404 if student unknown
    @GetMapping("/students/{studentId}/applications")
    public ResponseEntity<List<ApplicationResponse>> listForStudent(
            @PathVariable @NotNull @Positive Long studentId) {
        List<ApplicationResponse> body = applicationService.listForStudent(studentId).stream()
                .map(ApplicationResponse::from).toList();
        return ResponseEntity.ok(body);
    }

    // Student cancels a pending application -> 204 No Content
    // 404 if not found, 422 if it was already reviewed
    @DeleteMapping("/applications/{id}")
    public ResponseEntity<Void> cancel(@PathVariable @NotNull @Positive Long id) {
        applicationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
