package com.example.dostavka.repositories;

import com.example.dostavka.models.Stop;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends JpaRepository<Stop, Long> {
  List<Stop> findByRouteIdOrderBySequenceNumberAsc(Long routeId);
}
