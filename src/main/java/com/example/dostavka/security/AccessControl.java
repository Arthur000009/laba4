package com.example.dostavka.security;

import com.example.dostavka.repositories.ShipmentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("access")
public class AccessControl {
  private final ShipmentRepository shipments;

  public AccessControl(ShipmentRepository shipments) {
    this.shipments = shipments;
  }

  public Long currentId() {
    return ((AccountPrincipal)
            SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .getId();
  }

  public boolean dispatcher() {
    var a = SecurityContextHolder.getContext().getAuthentication();
    return a != null
        && a.getAuthorities().stream().anyMatch(x -> x.getAuthority().equals("ROLE_DISPATCHER"));
  }

  public boolean user(Long id) {
    return dispatcher() || currentId().equals(id);
  }

  public boolean sender(Long id) {
    return dispatcher() || id == null || currentId().equals(id);
  }

  public boolean shipment(Long id) {
    return dispatcher() || shipments.existsByIdAndSenderId(id, currentId());
  }
}
