package com.example.dostavka.models;

import jakarta.persistence.*;

/** Служебная строка блокировки, не ресурс REST API. */
@Entity
@Table(name = "operation_guard")
public class OperationGuard {
  @Id private Long id;
}
