package com.example.dostavka.security;

import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, JsonSecurityErrors errors)
      throws Exception {
    return http.csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()))
        .httpBasic(
            basic ->
                basic
                    .authenticationEntryPoint(errors)
                    .securityContextRepository(new NullSecurityContextRepository()))
        .securityContext(
            context -> context.securityContextRepository(new NullSecurityContextRepository()))
// Повторная Basic-аутентификация не должна сбрасывать CSRF-токен текущей сессии.
        .sessionManagement(session -> session.requireExplicitAuthenticationStrategy(true))
        .requestCache(cache -> cache.disable())
        .formLogin(form -> form.disable())
        .logout(logout -> logout.disable())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(errors).accessDeniedHandler(errors))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/api/csrf")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/register")
                    .permitAll()
                    .requestMatchers(
                        "/api/routes",
                        "/api/routes/**",
                        "/api/stops",
                        "/api/stops/**",
                        "/api/shipment-assignments",
                        "/api/shipment-assignments/**")
                    .hasRole("DISPATCHER")
                    .requestMatchers(
                        "/api/users",
                        "/api/users/**",
                        "/api/shipments",
                        "/api/shipments/**",
                        "/api/auth/me")
                    .authenticated()
                    .anyRequest()
                    .denyAll())
        .build();
  }
}
