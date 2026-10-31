package com.example.dostavka.repositories;

import com.example.dostavka.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  java.util.Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
