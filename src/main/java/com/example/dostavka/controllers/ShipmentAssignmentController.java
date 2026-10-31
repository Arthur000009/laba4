package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.models.ShipmentAssignment;
import com.example.dostavka.services.ShipmentAssignmentService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasRole('DISPATCHER')")
@RestController
@RequestMapping("/api/shipment-assignments")
public class ShipmentAssignmentController {

  private final ShipmentAssignmentService assignmentService;

  public ShipmentAssignmentController(ShipmentAssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @GetMapping
  public ResponseEntity<List<Responses.AssignmentResponse>> getAll() {
    return ResponseEntity.ok(assignmentService.getAllAssignments().stream().map(DtoMapper::assignmentResponse).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Responses.AssignmentResponse> getById(@PathVariable Long id) {
    ShipmentAssignment assignment = assignmentService.getAssignmentById(id);
    return assignment != null ? ResponseEntity.ok(DtoMapper.assignmentResponse(assignment)) : ResponseEntity.notFound().build();
  }

  @PostMapping
  public ResponseEntity<Responses.AssignmentResponse> create(
      @RequestParam Long shipmentId, @RequestParam Long stopId) {
    ShipmentAssignment created = assignmentService.createAssignment(shipmentId, stopId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.assignmentResponse(created));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Responses.AssignmentResponse> update(
      @PathVariable Long id, @RequestParam Long newStopId) {
    ShipmentAssignment updated = assignmentService.updateAssignmentStop(id, newStopId);
    return updated != null ? ResponseEntity.ok(DtoMapper.assignmentResponse(updated)) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    assignmentService.deleteAssignment(id);
    return ResponseEntity.noContent().build();
  }
}
