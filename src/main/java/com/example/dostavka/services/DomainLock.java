package com.example.dostavka.services;

import com.example.dostavka.models.OperationGuard;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.*;

/**
 * Блокировка берётся ДО чтения состояния и освобождается при commit/rollback. Работает между
 * экземплярами приложения, но сериализует все записи.
 */
@Component
public class DomainLock {
  private final EntityManager em;

  public DomainLock(EntityManager em) {
    this.em = em;
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void acquire() {
    if (em.find(OperationGuard.class, 1L, LockModeType.PESSIMISTIC_WRITE) == null)
      throw new IllegalStateException("Не инициализирована таблица блокировок");
  }
}
