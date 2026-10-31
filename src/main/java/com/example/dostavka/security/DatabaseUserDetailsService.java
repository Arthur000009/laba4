package com.example.dostavka.security;

import com.example.dostavka.repositories.UserRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
  private final UserRepository users;

  public DatabaseUserDetailsService(UserRepository users) {
    this.users = users;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    return users
        .findByEmail(email.trim().toLowerCase(Locale.ROOT))
        .map(AccountPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("Неверные учётные данные"));
  }
}
