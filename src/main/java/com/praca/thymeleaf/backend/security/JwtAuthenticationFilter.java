package com.praca.thymeleaf.backend.security;

import com.praca.thymeleaf.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("📌 Nagłówek Authorization: " + authHeader); // 🔥 DEBUG
        final String jwt;
        final String username;

        // Logowanie nagłówka Authorization
        System.out.println("[JwtAuthenticationFilter] Authorization Header: " + authHeader);

        // Sprawdź, czy nagłówek zawiera token JWT
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JwtAuthenticationFilter] No JWT token found in header or incorrect format.");
            filterChain.doFilter(request, response);
            return;
        }

        // Wyodrębnij token JWT
        jwt = authHeader.substring(7);
        System.out.println("✅ Token JWT: " + jwt);
        try {
            username = jwtUtil.extractUsername(jwt);
            System.out.println("[JwtAuthenticationFilter] Extracted Username: " + username);

            // Jeśli użytkownik nie jest uwierzytelniony, wykonaj weryfikację tokena
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    System.out.println("[JwtAuthenticationFilter] JWT token is valid. Setting authentication context.");
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    System.out.println("[JwtAuthenticationFilter] JWT token is invalid.");
                }
            } else {
                System.out.println("[JwtAuthenticationFilter] User is already authenticated or username is null.");
            }
        } catch (Exception e) {
            System.out.println("[JwtAuthenticationFilter] Exception during JWT processing: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
