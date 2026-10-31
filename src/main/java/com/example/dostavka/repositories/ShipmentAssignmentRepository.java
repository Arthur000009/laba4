package com.example.dostavka.repositories;

import com.example.dostavka.models.ShipmentAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentAssignmentRepository extends JpaRepository<ShipmentAssignment, Long> {
  Optional<ShipmentAssignment> findByShipmentId(Long shipmentId);

  List<ShipmentAssignment> findByStopId(Long stopId);

  List<ShipmentAssignment> findByStopRouteId(Long routeId);
}
