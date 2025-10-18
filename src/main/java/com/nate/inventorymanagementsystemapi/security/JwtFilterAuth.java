package com.nate.inventorymanagementsystemapi.security;

import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtFilterAuth extends OncePerRequestFilter {

    private final UserDetailsService service;
    private static final String[] EXCLUDED_PATHS = {
            "/auth/login",
            "/auth/register",
            "/swagger-ui",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/swagger-ui/index.html",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-resources",
            "/swagger-resources/",
            "/webjars/"
    };



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        if(isExcluded(path)){
            filterChain.doFilter(request,response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization");
            return;
        }

        String token = authHeader.substring(7);

        if(!JwtUtil.tokenValidation(token)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or Expired token");
            return;
        }

        CustomerDetails customerDetails = (CustomerDetails) service.loadUserByUsername(JwtUtil.extractUsername(token));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customerDetails, null, customerDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request,response);

    }

    private boolean isExcluded(String path) {
        for (String exclude : EXCLUDED_PATHS) {
            if (path.contains(exclude)) {
                return true;
            }
        }
        return false;
    }

}
