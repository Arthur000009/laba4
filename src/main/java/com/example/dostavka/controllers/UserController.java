package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.dto.Requests;
import com.example.dostavka.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService users;

  public UserController(UserService users) {
    this.users = users;
  }

  @GetMapping
  @PreAuthorize("hasRole('DISPATCHER')")
  public List<Responses.UserResponse> getAll() {
    return users.getAllUsers().stream().map(DtoMapper::userResponse).toList();
  }

  @GetMapping("/{id}")
  @PreAuthorize("@access.user(#id)")
  public ResponseEntity<Responses.UserResponse> get(@PathVariable Long id) {
    return ResponseEntity.ofNullable(DtoMapper.userResponse(users.getUserById(id)));
  }

  @PostMapping
  @PreAuthorize("hasRole('DISPATCHER')")
  public ResponseEntity<Responses.UserResponse> create(@Valid @RequestBody Requests.Registration r) {
    return ResponseEntity.status(201).body(DtoMapper.userResponse(users.createUser(r)));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("@access.user(#id)")
  public ResponseEntity<Responses.UserResponse> update(
      @PathVariable Long id, @Valid @RequestBody Requests.UserPatch r) {
    return ResponseEntity.ofNullable(DtoMapper.userResponse(users.updateUser(id, r)));
  }

  @PatchMapping("/{id}/account-status")
  @PreAuthorize("hasRole('DISPATCHER')")
  public ResponseEntity<Responses.UserResponse> status(
      @PathVariable Long id, @Valid @RequestBody Requests.AccountState r) {
    return ResponseEntity.ofNullable(DtoMapper.userResponse(users.updateAccountStatus(id, r.accountStatus())));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('DISPATCHER')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    users.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
