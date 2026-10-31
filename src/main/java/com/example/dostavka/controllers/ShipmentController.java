package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.dto.Requests;
import com.example.dostavka.security.AccessControl;
import com.example.dostavka.services.ShipmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
  private final ShipmentService shipments;
  private final AccessControl access;

  public ShipmentController(ShipmentService shipments, AccessControl access) {
    this.shipments = shipments;
    this.access = access;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<Responses.ShipmentResponse> getAll() {
    return (access.dispatcher()
            ? shipments.getAllShipments()
            : shipments.getShipmentsBySender(access.currentId()))
        .stream()
        .map(DtoMapper::shipmentResponse)
        .toList();
  }

  @GetMapping("/{id}")
  @PreAuthorize("@access.shipment(#id)")
  public ResponseEntity<Responses.ShipmentResponse> get(@PathVariable Long id) {
    return ResponseEntity.ofNullable(DtoMapper.shipmentResponse(shipments.getShipmentById(id)));
  }

  @PostMapping
  @PreAuthorize("@access.sender(#senderId)")
  public ResponseEntity<Responses.ShipmentResponse> create(
      @Valid @RequestBody Requests.ShipmentCreate r,
      @RequestParam(required = false) Long senderId) {
    return ResponseEntity.status(201)
        .body(
            DtoMapper.shipmentResponse(
                shipments.createShipment(
                    DtoMapper.shipment(r), senderId == null ? access.currentId() : senderId)));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@access.shipment(#id)")
  public ResponseEntity<Responses.ShipmentResponse> update(
      @PathVariable Long id, @Valid @RequestBody Requests.ShipmentPatch r) {
    return ResponseEntity.ofNullable(DtoMapper.shipmentResponse(shipments.updateShipment(id, DtoMapper.shipment(r))));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("@access.shipment(#id)")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    shipments.deleteShipment(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{shipmentId}/transfer")
  @PreAuthorize("hasRole('DISPATCHER')")
  public Responses.AssignmentResponse transfer(@PathVariable Long shipmentId, @RequestParam Long newStopId) {
    return DtoMapper.assignmentResponse(shipments.transferShipment(shipmentId, newStopId));
  }

  @PostMapping("/{shipmentId}/cancel")
  @PreAuthorize("@access.shipment(#shipmentId)")
  public Responses.ShipmentResponse cancel(@PathVariable Long shipmentId) {
    return DtoMapper.shipmentResponse(shipments.cancelShipment(shipmentId));
  }
}
