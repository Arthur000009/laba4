package com.example.dostavka.dto;

import com.example.dostavka.models.AccountStatus;
import com.example.dostavka.models.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class Responses {
  private Responses() {}

  public record UserResponse(
      Long id, String name, String email, Role role, AccountStatus accountStatus) {}

  public record RouteResponse(Long id, LocalDate date, Double maxWeight, String status) {}

  public record StopResponse(
      Long id, RouteResponse route, String address, Integer sequenceNumber) {}

  public record ShipmentResponse(
      Long id,
      LocalDateTime createdAt,
      UserResponse sender,
      String address,
      Double weight,
      String status) {}

  public record AssignmentResponse(
      Long id, ShipmentResponse shipment, StopResponse stop, String status) {}
}
