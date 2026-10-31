package com.example.dostavka.controllers;

import java.util.Map;
import org.springframework.dao.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private ResponseEntity<?> error(int status, String code, String message) {
    return ResponseEntity.status(status).body(Map.of("code", code, "message", message));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> argument(IllegalArgumentException e) {
    return error(
        400, "INVALID_REQUEST", e.getMessage() == null ? "Некорректный запрос" : e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> state(IllegalStateException e) {
    return error(
        409,
        "BUSINESS_CONFLICT",
        e.getMessage() == null ? "Операция недоступна в текущем состоянии" : e.getMessage());
  }

  @ExceptionHandler({
    MethodArgumentNotValidException.class,
    HttpMessageNotReadableException.class,
    MissingServletRequestParameterException.class,
    MethodArgumentTypeMismatchException.class
  })
  public ResponseEntity<?> invalid(Exception e) {
    return error(
        400,
        "INVALID_REQUEST",
        "Проверьте поля запроса; неизвестные и служебные поля не допускаются");
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> constraint(Exception e) {
    return error(409, "DATA_CONFLICT", "Нарушено ограничение уникальности или целостности данных");
  }

  @ExceptionHandler(PessimisticLockingFailureException.class)
  public ResponseEntity<?> concurrent(Exception e) {
    return error(
        409, "CONCURRENT_MODIFICATION", "Данные изменяются другим запросом; повторите операцию");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> denied(Exception e) {
    return error(403, "FORBIDDEN", "Недостаточно прав для выполнения операции");
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> unexpected(Exception e) {
    if (e instanceof ErrorResponse response
            && response.getStatusCode().is4xxClientError()) {
      return ResponseEntity.status(response.getStatusCode())
              .headers(response.getHeaders())
              .body(Map.of(
                      "code", "INVALID_REQUEST",
                      "message", "Некорректный HTTP-запрос"));
    }
    return error(500, "INTERNAL_ERROR", "Внутренняя ошибка сервера");
  }
}
