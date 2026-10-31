package com.example.dostavka.security;

import com.example.dostavka.models.*;
import com.example.dostavka.repositories.UserRepository;
import com.example.dostavka.services.DomainLock;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true")
public class BootstrapDispatcher implements ApplicationRunner {
  private final UserRepository users;
  private final DomainLock lock;
  private final PasswordPolicy policy;
  private final PasswordEncoder encoder;

  @Value("${app.bootstrap.email:}")
  private String email;

  @Value("${app.bootstrap.password:}")
  private String password;

  public BootstrapDispatcher(
      UserRepository users, DomainLock lock, PasswordPolicy policy, PasswordEncoder encoder) {
    this.users = users;
    this.lock = lock;
    this.policy = policy;
    this.encoder = encoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    if (email.isBlank() || !email.contains("@"))
      throw new IllegalStateException("Задайте email диспетчера через env");
    policy.validate(password);
    String normalized = email.trim().toLowerCase(Locale.ROOT);
    String hash = encoder.encode(password);
    lock.acquire();
    var existing = users.findByEmail(normalized);
    if (existing.isPresent()) {
      if (existing.get().getRole() != Role.DISPATCHER)
        throw new IllegalStateException(
            "Bootstrap email принадлежит клиенту; повышение роли запрещено");
      return;
    }
    User u = new User();
    u.setName("Диспетчер");
    u.setEmail(normalized);
    u.setPasswordHash(hash);
    u.setRole(Role.DISPATCHER);
    u.setAccountStatus(AccountStatus.ACTIVE);
    users.save(u);
  }
}
