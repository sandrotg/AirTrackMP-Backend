package com.airtrackmp.iot.airtrackmp.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ApiErrorWriter apiErrorWriter;

    public JwtFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            ApiErrorWriter apiErrorWriter
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.apiErrorWriter = apiErrorWriter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("AUTH HEADER: " + request.getHeader("Authorization"));

        final String authHeader =
                request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractUsername(token);

            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails user = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (JwtException | UsernameNotFoundException exception) {
            apiErrorWriter.write(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    "Token inválido o expirado",
                    request.getRequestURI()
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}