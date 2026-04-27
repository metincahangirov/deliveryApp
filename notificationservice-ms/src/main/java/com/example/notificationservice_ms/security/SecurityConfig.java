package com.example.notificationservice_ms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JsonAuthResponseWriter jsonAuthResponseWriter,
            CorsConfigurationSource corsConfigurationSource
    ) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                jsonAuthResponseWriter.write(
                                        response,
                                        HttpStatus.UNAUTHORIZED,
                                        "Autentifikasiya tələb olunur. Header: Authorization: Bearer <JWT>.",
                                        request.getRequestURI()
                                ))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                jsonAuthResponseWriter.write(
                                        response,
                                        HttpStatus.FORBIDDEN,
                                        "Bu resursa giriş icazəsi yoxdur.",
                                        request.getRequestURI()
                                ))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/notifications").permitAll()
                        .requestMatchers("/api/notifications/me", "/api/notifications/me/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
