package com.student.bus_pass_tracker.service;

import com.student.bus_pass_tracker.dto.RouteRequest;
import com.student.bus_pass_tracker.entity.BusRoute;
import com.student.bus_pass_tracker.exception.BusinessRuleException;
import com.student.bus_pass_tracker.exception.NotFoundException;
import com.student.bus_pass_tracker.repository.BusRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BusRouteService {

    private final BusRouteRepository routes;

    public BusRouteService(BusRouteRepository routes) {
        this.routes = routes;
    }

    public BusRoute createRoute(RouteRequest req) {
        // Rule: route code must be unique. Checked before saving.
        if (routes.existsByRouteCode(req.routeCode())) {
            throw new BusinessRuleException("Route code already exists: " + req.routeCode());
        }
        BusRoute route = new BusRoute();
        route.setRouteCode(req.routeCode());
        route.setRouteName(req.routeName());
        route.setStartPoint(req.startPoint());
        route.setEndPoint(req.endPoint());
        route.setBoardingPoints(req.boardingPoints());
        return routes.save(route);
    }

    @Transactional(readOnly = true)
    public BusRoute getRoute(Long id) {
        return routes.findById(id)
                .orElseThrow(() -> new NotFoundException("Route not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<BusRoute> listRoutes() {
        return routes.findAll();
    }
}
