package com.nate.inventorymanagementsystemapi.security;

import jakarta.xml.ws.WebEndpoint;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilterAuth filterAuth) throws Exception {
        http
                .csrf(c-> c.disable())
                .sessionManagement(sm-> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login","/auth/register","/v3/api-docs/**","/swagger-ui/**", "/swagger-ui.html","/swagger-resources/**"
                        ,"/webjars/**").permitAll()
                        .requestMatchers("/product/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/product/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/auth/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                ).addFilterBefore(filterAuth, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
}
