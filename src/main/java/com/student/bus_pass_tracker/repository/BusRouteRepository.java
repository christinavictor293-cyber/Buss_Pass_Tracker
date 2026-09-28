package com.student.bus_pass_tracker.repository;

import com.student.bus_pass_tracker.entity.BusRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusRouteRepository extends JpaRepository<BusRoute, Long> {

    boolean existsByRouteCode(String routeCode);
}
