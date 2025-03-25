package com.recipes.FlavourFoundry.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.recipes.FlavourFoundry.service.CustomUserDetailsService;
import com.recipes.FlavourFoundry.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JWTService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    // every request to spring boot will first pass through this jwt filter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        String requestMethod = request.getMethod();
        System.out.println("Request Type: " + requestMethod);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        System.out.println("token: " + token);

        // authentication will always be null for each new request due to stateless session settings 
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                email = jwtService.extractEmail(token);
                System.out.println("Authenticated: " + email);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                
                // checks with user is registered in database and token is valid
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
