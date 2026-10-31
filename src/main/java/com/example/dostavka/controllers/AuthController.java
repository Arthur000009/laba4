package com.example.dostavka.controllers;

import com.example.dostavka.dto.Responses;
import com.example.dostavka.dto.Requests;
import com.example.dostavka.security.AccessControl;
import com.example.dostavka.services.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
  private final UserService users;
  private final AccessControl access;

  public AuthController(UserService users, AccessControl access) {
    this.users = users;
    this.access = access;
  }

  @GetMapping("/csrf")
  public Map<String, String> csrf(CsrfToken t) {
    return Map.of(
        "token",
        t.getToken(),
        "headerName",
        t.getHeaderName(),
        "parameterName",
        t.getParameterName());
  }

  @PostMapping("/auth/register")
  public ResponseEntity<Responses.UserResponse> register(@Valid @RequestBody Requests.Registration r) {
    return ResponseEntity.status(201).body(DtoMapper.userResponse(users.createUser(r)));
  }

  @GetMapping("/auth/me")
  @PreAuthorize("isAuthenticated()")
  public Responses.UserResponse me() {
    return DtoMapper.userResponse(users.getUserById(access.currentId()));
  }
}
