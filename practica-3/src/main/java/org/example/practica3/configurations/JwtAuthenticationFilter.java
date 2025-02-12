package org.example.practica3.configurations;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.practica3.entities.Mockup;
import org.example.practica3.services.JwtService;
import org.example.practica3.services.MockupService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Order(1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MockupService mockupService;

    public JwtAuthenticationFilter(JwtService jwtService, MockupService mockupService) {
        this.jwtService = jwtService;
        this.mockupService = mockupService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI().replaceFirst("/mockup-server/", "");
        String method = request.getMethod();

        Optional<Mockup> mockupOpt = mockupService.findByPath(path);

        if (mockupOpt.isPresent() && mockupOpt.get().isRequiresJwt()) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("JWT token is required");
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtService.validateToken(token, path, method)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired JWT token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/mockup-server/") || path.equals("/mockup-server/");
    }
}