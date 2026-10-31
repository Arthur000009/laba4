package com.example.dostavka.security;

import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicy {
  public void validate(String p) {
    if (p == null
        || p.codePointCount(0, p.length()) < 12
        || p.getBytes(StandardCharsets.UTF_8).length > 72
        || p.codePoints().noneMatch(Character::isUpperCase)
        || p.codePoints().noneMatch(Character::isLowerCase)
        || p.codePoints().noneMatch(Character::isDigit)
        || p.codePoints().noneMatch(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c)))
      throw new IllegalArgumentException(
          "Пароль: минимум 12 символов, максимум 72 байта UTF-8, верхний и нижний регистр, цифра и"
              + " спецсимвол");
  }
}
