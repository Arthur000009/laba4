package com.example.dostavka.repositories;

import com.example.dostavka.models.Shipment;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
  List<Shipment> findBySenderId(Long senderId);

  boolean existsByIdAndSenderId(Long id, Long senderId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Shipment s WHERE s.id = :id")
  Optional<Shipment> findByIdWithLock(@Param("id") Long id);
}
