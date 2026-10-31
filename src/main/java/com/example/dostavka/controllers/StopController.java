package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.dto.Requests;
import com.example.dostavka.models.ShipmentAssignment;
import com.example.dostavka.models.Stop;
import com.example.dostavka.services.StopService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('DISPATCHER')")
@RestController
@RequestMapping("/api/stops")
public class StopController {

  private final StopService stopService;

  public StopController(StopService stopService) {
    this.stopService = stopService;
  }

  @GetMapping
  public ResponseEntity<List<Responses.StopResponse>> getAll() {
    return ResponseEntity.ok(stopService.getAllStops().stream().map(DtoMapper::stopResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Responses.StopResponse> getById(@PathVariable Long id) {
    Stop stop = stopService.getStopById(id);
    return stop != null ? ResponseEntity.ok(DtoMapper.stopResponse(stop)) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<Responses.StopResponse> create(
      @Valid @RequestBody Requests.StopCreate stop, @RequestParam Long routeId) {
    Stop created = stopService.createStop(DtoMapper.stop(stop), routeId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.stopResponse(created));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Responses.StopResponse> update(
      @PathVariable Long id, @Valid @RequestBody Requests.StopPatch updates) {
    Stop updated = stopService.updateStop(id, DtoMapper.stop(updates));
    return updated != null ? ResponseEntity.ok(DtoMapper.stopResponse(updated)) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    stopService.deleteStop(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{stopId}/deliver")
  public ResponseEntity<List<Responses.AssignmentResponse>> deliverAtStop(@PathVariable Long stopId) {
    List<ShipmentAssignment> delivered = stopService.completeStopDelivery(stopId);
    return ResponseEntity.ok(delivered.stream().map(DtoMapper::assignmentResponse).toList());
  }
}
