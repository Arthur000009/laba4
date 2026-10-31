package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.dto.Requests;
import com.example.dostavka.models.Route;
import com.example.dostavka.models.ShipmentAssignment;
import com.example.dostavka.services.RouteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('DISPATCHER')")
@RestController
@RequestMapping("/api/routes")
public class RouteController {

  private final RouteService routeService;

  public RouteController(RouteService routeService) {
    this.routeService = routeService;
  }

  @GetMapping
  public ResponseEntity<List<Responses.RouteResponse>> getAll() {
    return ResponseEntity.ok(routeService.getAllRoutes().stream().map(DtoMapper::routeResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Responses.RouteResponse> getById(@PathVariable Long id) {
    Route route = routeService.getRouteById(id);
    return route != null ? ResponseEntity.ok(DtoMapper.routeResponse(route)) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<Responses.RouteResponse> create(@Valid @RequestBody Requests.RouteCreate route) {
    Route created = routeService.createRoute(DtoMapper.route(route));
    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.routeResponse(created));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Responses.RouteResponse> update(
      @PathVariable Long id, @Valid @RequestBody Requests.RoutePatch updates) {
    Route updated = routeService.updateRoute(id, DtoMapper.route(updates));
    return updated != null ? ResponseEntity.ok(DtoMapper.routeResponse(updated)) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    routeService.deleteRoute(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{routeId}/assign-shipment")
  public ResponseEntity<Responses.AssignmentResponse> assignShipment(
      @PathVariable Long routeId, @RequestParam Long stopId, @RequestParam Long shipmentId) {
    ShipmentAssignment assignment =
        routeService.assignShipmentToRouteStop(routeId, stopId, shipmentId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.assignmentResponse(assignment));
  }

  @PostMapping("/{routeId}/start")
  public ResponseEntity<Responses.RouteResponse> startRoute(@PathVariable Long routeId) {
    Route started = routeService.startRoute(routeId);
    return ResponseEntity.ok(DtoMapper.routeResponse(started));
  }
}
