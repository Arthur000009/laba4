package com.example.dostavka.security;

import com.example.dostavka.models.*;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AccountPrincipal extends org.springframework.security.core.userdetails.User {
  private final Long id;

  public AccountPrincipal(User u) {
    super(
        u.getEmail(),
        u.getPasswordHash(),
        u.getAccountStatus() == AccountStatus.ACTIVE,
        true,
        true,
        u.getAccountStatus() != AccountStatus.BLOCKED,
        List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name())));
    id = u.getId();
  }

  public Long getId() {
    return id;
  }
}
