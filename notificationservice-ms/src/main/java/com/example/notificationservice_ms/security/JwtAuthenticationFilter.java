package com.example.notificationservice_ms.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";

    private final JwtService jwtService;
    private final JsonAuthResponseWriter jsonAuthResponseWriter;

    public JwtAuthenticationFilter(JwtService jwtService, JsonAuthResponseWriter jsonAuthResponseWriter) {
        this.jwtService = jwtService;
        this.jsonAuthResponseWriter = jsonAuthResponseWriter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader(AUTH_HEADER);

        if (header != null && !header.isBlank()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UUID userId = jwtService.parseUserId(header);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException | JwtException ex) {
                jsonAuthResponseWriter.write(
                        response,
                        HttpStatus.UNAUTHORIZED,
                        "JWT token etibarsızdır və ya vaxtı keçib.",
                        request.getRequestURI()
                );
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = pathWithoutContext(request);
        String method = request.getMethod();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/h2-console")
                || path.startsWith("/api/auth/")
                || (method.equals("POST") && path.equals("/api/notifications"))
                || path.equals("/error")
                || "OPTIONS".equalsIgnoreCase(method);
    }

    private static String pathWithoutContext(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String context = request.getContextPath();
        if (context != null && !context.isEmpty() && uri.startsWith(context)) {
            return uri.substring(context.length());
        }
        return uri;
    }
}
