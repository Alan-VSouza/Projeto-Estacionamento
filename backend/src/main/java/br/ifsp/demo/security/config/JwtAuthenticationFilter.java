package br.ifsp.demo.security.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("DEBUG: JwtAuthenticationFilter - Request URI: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("DEBUG: JwtAuthenticationFilter - Authorization Header: " + authHeader);

        final String prefix = "Bearer ";
        if (authHeader == null || !authHeader.startsWith(prefix)) {
            System.out.println("DEBUG: JwtAuthenticationFilter - No Bearer token found or header is null. Passing to next filter.");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(prefix.length());
        System.out.println("DEBUG: JwtAuthenticationFilter - Extracted JWT: " + jwt);

        String email = null;
        try {
            email = jwtService.extractUsername(jwt);
            System.out.println("DEBUG: JwtAuthenticationFilter - Email extracted from JWT: " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("DEBUG: JwtAuthenticationFilter - Attempting to load UserDetails for email: " + email);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (userDetails != null) {
                    System.out.println("DEBUG: JwtAuthenticationFilter - UserDetails loaded successfully for: " + userDetails.getUsername());
                    boolean isTokenValid = jwtService.isTokenValid(jwt, userDetails);
                    System.out.println("DEBUG: JwtAuthenticationFilter - Is token valid? " + isTokenValid);

                    if (isTokenValid) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("DEBUG: JwtAuthenticationFilter - User authenticated and set in SecurityContext: " + email);
                    } else {
                        System.out.println("DEBUG: JwtAuthenticationFilter - Token deemed invalid for user: " + email);
                    }
                } else {
                    System.out.println("DEBUG: JwtAuthenticationFilter - UserDetails NOT found for email: " + email);
                }
            } else {
                if (email == null) {
                    System.out.println("DEBUG: JwtAuthenticationFilter - Email could not be extracted from JWT.");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("DEBUG: JwtAuthenticationFilter - SecurityContextHolder already contains an authentication.");
                }
            }
        } catch (ExpiredJwtException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - JWT Token has expired for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT Token has expired.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (SignatureException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - JWT Signature is invalid for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT Signature is invalid.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (MalformedJwtException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - JWT is malformed for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT is malformed.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (UnsupportedJwtException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - JWT is unsupported for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT is unsupported.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (IllegalArgumentException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - JWT string is empty or null for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT string is invalid.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (JwtException e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - General JWT error for email '" + email + "'. Message: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"JWT is not valid.\", \"detail\": \"" + e.getMessage().replace("\"", "'") + "\"}");
            return;
        } catch (Exception e) {
            System.err.println("DEBUG: JwtAuthenticationFilter - Unexpected error during JWT processing for email '" + email + "'. Message: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"An unexpected error occurred during authentication processing.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
