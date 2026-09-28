package com.student.bus_pass_tracker.controller;

import com.student.bus_pass_tracker.dto.RouteRequest;
import com.student.bus_pass_tracker.entity.BusRoute;
import com.student.bus_pass_tracker.service.BusRouteService;
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
@RequestMapping("/api/routes")
@Validated
public class BusRouteController {

    private final BusRouteService routeService;

    public BusRouteController(BusRouteService routeService) {
        this.routeService = routeService;
    }

    // Admin uploads route details: 201 Created + Location header
    @PostMapping
    public ResponseEntity<BusRoute> create(@Valid @RequestBody RouteRequest request) {
        BusRoute saved = routeService.createRoute(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusRoute> get(@PathVariable @NotNull @Positive Long id) {
        return ResponseEntity.ok(routeService.getRoute(id));
    }

    @GetMapping
    public ResponseEntity<List<BusRoute>> list() {
        return ResponseEntity.ok(routeService.listRoutes());
    }
}