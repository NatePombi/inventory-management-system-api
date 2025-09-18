package com.nate.inventorymanagementsystemapi.security;

import com.nate.inventorymanagementsystemapi.model.CustomerDetails;
import com.nate.inventorymanagementsystemapi.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class JwtFilterAuthTest {

    private JwtFilterAuth jwtFilterAuth;
    private UserDetailsService detailsService;
    private HttpServletResponse response;
    private HttpServletRequest request;
    private FilterChain filterChain;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void startUp(){
        detailsService = mock(UserDetailsService.class);
        jwtFilterAuth = new JwtFilterAuth(detailsService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        try {
            when(response.getWriter()).thenReturn(printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldPassThroughAuthEndPoints() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/login");
        jwtFilterAuth.doFilterInternal(request,response,filterChain);
        verify(filterChain, times(1)).doFilter(request,response);
    }

    @Test
    void shouldReturn401WhenMissingAuthorization() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/product/1");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilterAuth.doFilterInternal(request,response,filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request,response);
    }

    @Test
    void shouldReturn401WhenTokenInvalid() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/product/1");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");

        try (MockedStatic<JwtUtil> jwtUtilMockedStatic = Mockito.mockStatic(JwtUtil.class)){
            jwtUtilMockedStatic.when(()-> JwtUtil.tokenValidation("invalidToken")).thenReturn(false);

            jwtFilterAuth.doFilterInternal(request,response,filterChain);
        }


        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request,response);
    }

    @Test
    void shouldAuthenticationWhenTokenValid() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/product/1");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");

        CustomerDetails customerDetails = mock(CustomerDetails.class);
        when(customerDetails.getAuthorities()).thenReturn(null);

        when(detailsService.loadUserByUsername("tester")).thenReturn(customerDetails);

        try (MockedStatic<JwtUtil> jwtMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtMock.when(()-> JwtUtil.tokenValidation("validToken")).thenReturn(true);
            jwtMock.when(()-> JwtUtil.extractUsername("validToken")).thenReturn("tester");

            jwtFilterAuth.doFilterInternal(request,response,filterChain);
        }

        verify(filterChain).doFilter(request,response);
    }

}
