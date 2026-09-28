package com.student.bus_pass_tracker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Entity
public class BusRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String routeCode;

    @NotBlank
    @Column(nullable = false)
    private String routeName;

    @NotBlank
    @Column(nullable = false)
    private String startPoint;

    @NotBlank
    @Column(nullable = false)
    private String endPoint;

    // Valid stops a student can choose as their boarding point
    @NotEmpty
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> boardingPoints = new ArrayList<>();

    // One route -> many pass applications (inverse side, no column created)
    @OneToMany(mappedBy = "route")
    @JsonIgnore
    private List<PassApplication> applications = new ArrayList<>();

    public BusRoute() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }
    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }
    public String getStartPoint() { return startPoint; }
    public void setStartPoint(String startPoint) { this.startPoint = startPoint; }
    public String getEndPoint() { return endPoint; }
    public void setEndPoint(String endPoint) { this.endPoint = endPoint; }
    public List<String> getBoardingPoints() { return boardingPoints; }
    public void setBoardingPoints(List<String> boardingPoints) { this.boardingPoints = boardingPoints; }
    public List<PassApplication> getApplications() { return applications; }
    public void setApplications(List<PassApplication> applications) { this.applications = applications; }
}