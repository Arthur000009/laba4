package com.example.dostavka.dto;

import com.example.dostavka.models.AccountStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/** DTO содержат только разрешённые клиенту поля. */
public final class Requests {
  private Requests() {}

  public record Registration(
      @NotBlank @Size(max = 255) String name,
      @NotBlank @Email @Size(max = 255) String email,
      @NotNull String password) {}

  public record UserPatch(
      @Pattern(regexp = "(?s).*\\S.*") @Size(max = 255) String name,
      @Email @Pattern(regexp = "(?s).*\\S.*") @Size(max = 255) String email) {}

  public record AccountState(@NotNull AccountStatus accountStatus) {}

  public record RouteCreate(@NotNull LocalDate date, @NotNull @Positive Double maxWeight) {}

  public record RoutePatch(LocalDate date, @Positive Double maxWeight) {}

  public record ShipmentCreate(
      @NotBlank @Size(max = 255) String address, @NotNull @Positive Double weight) {}

  public record ShipmentPatch(
      @Pattern(regexp = "(?s).*\\S.*") @Size(max = 255) String address, @Positive Double weight) {}

  public record StopCreate(
      @NotBlank @Size(max = 255) String address, @NotNull @Positive Integer sequenceNumber) {}

  public record StopPatch(
      @Pattern(regexp = "(?s).*\\S.*") @Size(max = 255) String address,
      @Positive Integer sequenceNumber) {}
}
