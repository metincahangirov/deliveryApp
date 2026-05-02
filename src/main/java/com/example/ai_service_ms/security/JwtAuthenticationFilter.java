package com.example.ai_service_ms.security;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Validates JWT on every request, binds {@link FoodDeliveryPrincipal} to the security context.
 * The caller's identity is always taken from {@code sub}, never from query/path parameters.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	public static final String ATTR_RAW_JWT = "com.example.ai_service_ms.RAW_JWT";

	private final JwtTokenValidator jwtTokenValidator;

	public JwtAuthenticationFilter(JwtTokenValidator jwtTokenValidator) {
		this.jwtTokenValidator = jwtTokenValidator;
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		String path = request.getServletPath();
		if (path == null) {
			return false;
		}
		return path.startsWith("/actuator/")
				|| path.startsWith("/swagger-ui")
				|| path.startsWith("/v3/api-docs");
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null || header.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String rawJwt = JwtTokenValidator.stripBearerPrefix(header);
			UUID userId = jwtTokenValidator.extractUserId(rawJwt);
			request.setAttribute(ATTR_RAW_JWT, rawJwt);

			FoodDeliveryPrincipal principal = new FoodDeliveryPrincipal(userId);
			var auth = new UsernamePasswordAuthenticationToken(principal, rawJwt, principal.getAuthorities());
			auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		catch (JwtTokenValidator.InvalidJwtException ex) {
			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"Invalid or missing JWT\"}");
			return;
		}

		filterChain.doFilter(request, response);
	}
}
