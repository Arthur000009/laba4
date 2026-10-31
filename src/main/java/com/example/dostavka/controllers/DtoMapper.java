package com.example.dostavka.controllers;

import com.example.dostavka.dto.Requests;
import com.example.dostavka.dto.Responses;
import com.example.dostavka.models.*;

final class DtoMapper {
  private DtoMapper() {}

  static Route route(Requests.RouteCreate x) {
    finite(x.maxWeight());
    return new Route(null, x.date(), x.maxWeight(), null);
  }

  static Route route(Requests.RoutePatch x) {
    finite(x.maxWeight());
    return new Route(null, x.date(), x.maxWeight(), null);
  }

  static Stop stop(Requests.StopCreate x) {
    return new Stop(null, null, x.address(), x.sequenceNumber());
  }

  static Stop stop(Requests.StopPatch x) {
    return new Stop(null, null, x.address(), x.sequenceNumber());
  }

  static Shipment shipment(Requests.ShipmentCreate x) {
    finite(x.weight());
    return new Shipment(null, null, null, x.address(), x.weight(), null);
  }

  static Shipment shipment(Requests.ShipmentPatch x) {
    finite(x.weight());
    return new Shipment(null, null, null, x.address(), x.weight(), null);
  }

  static Responses.UserResponse userResponse(User value) {
    if (value == null) return null;
    return new Responses.UserResponse(
        value.getId(), value.getName(), value.getEmail(), value.getRole(), value.getAccountStatus());
  }

  static Responses.RouteResponse routeResponse(Route value) {
    if (value == null) return null;
    return new Responses.RouteResponse(
        value.getId(), value.getDate(), value.getMaxWeight(), value.getStatus());
  }

  static Responses.StopResponse stopResponse(Stop value) {
    if (value == null) return null;
    return new Responses.StopResponse(
        value.getId(), routeResponse(value.getRoute()), value.getAddress(), value.getSequenceNumber());
  }

  static Responses.ShipmentResponse shipmentResponse(Shipment value) {
    if (value == null) return null;
    return new Responses.ShipmentResponse(
        value.getId(),
        value.getCreatedAt(),
        userResponse(value.getSender()),
        value.getAddress(),
        value.getWeight(),
        value.getStatus());
  }

  static Responses.AssignmentResponse assignmentResponse(ShipmentAssignment value) {
    if (value == null) return null;
    return new Responses.AssignmentResponse(
        value.getId(), shipmentResponse(value.getShipment()), stopResponse(value.getStop()),
        value.getStatus());
  }

  private static void finite(Double x) {
    if (x != null && !Double.isFinite(x))
      throw new IllegalArgumentException("Вес должен быть конечным числом");
  }
}
