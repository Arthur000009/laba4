package com.example.dostavka.services;

import com.example.dostavka.dto.Requests;
import com.example.dostavka.models.*;
import com.example.dostavka.repositories.*;
import com.example.dostavka.security.PasswordPolicy;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
  private final UserRepository users;
  private final ShipmentRepository shipments;
  private final PasswordEncoder encoder;
  private final PasswordPolicy policy;
  private final DomainLock lock;

  public UserService(
      UserRepository users,
      ShipmentRepository shipments,
      PasswordEncoder encoder,
      PasswordPolicy policy,
      DomainLock lock) {
    this.users = users;
    this.shipments = shipments;
    this.encoder = encoder;
    this.policy = policy;
    this.lock = lock;
  }

  public List<User> getAllUsers() {
    return users.findAll();
  }

  public User getUserById(Long id) {
    return users.findById(id).orElse(null);
  }

  @Transactional
  public User createUser(Requests.Registration request) {
    policy.validate(request.password());
    String hash = encoder.encode(request.password());
    lock.acquire();
    String email = normalize(request.email());
    if (users.existsByEmail(email))
      throw new IllegalStateException("Учётная запись с таким email уже существует");
    User u = new User();
    u.setName(request.name().trim());
    u.setEmail(email);
    u.setPasswordHash(hash);
    u.setRole(Role.CLIENT);
    u.setAccountStatus(AccountStatus.ACTIVE);
    return users.save(u);
  }

  @Transactional
  public User updateUser(Long id, Requests.UserPatch request) {
    lock.acquire();
    User u = users.findById(id).orElse(null);
    if (u == null) return null;
    if (request.name() != null) u.setName(request.name().trim());
    if (request.email() != null) {
      String email = normalize(request.email());
      if (!email.equals(u.getEmail()) && users.existsByEmail(email))
        throw new IllegalStateException("Учётная запись с таким email уже существует");
      u.setEmail(email);
    }
    return users.save(u);
  }

  @Transactional
  public User updateAccountStatus(Long id, AccountStatus status) {
    lock.acquire();
    User u = users.findById(id).orElse(null);
    if (u == null) return null;
    if (u.getRole() != Role.CLIENT)
      throw new IllegalStateException("Через API можно блокировать только клиентов");
    u.setAccountStatus(status);
    return users.save(u);
  }

  @Transactional
  public void deleteUser(Long id) {
    lock.acquire();
    User u = users.findById(id).orElse(null);
    if (u == null) return;
    if (u.getRole() != Role.CLIENT)
      throw new IllegalStateException("Удаление диспетчера через API запрещено");
    if (shipments.findBySenderId(id).stream()
        .anyMatch(s -> !"DELIVERED".equals(s.getStatus()) && !"CANCELLED".equals(s.getStatus())))
      throw new IllegalStateException("Невозможно удалить пользователя с активными отправлениями");
    users.delete(u);
  }

  private String normalize(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }
}
