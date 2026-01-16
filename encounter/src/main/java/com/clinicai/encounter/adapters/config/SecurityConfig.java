package com.clinicai.encounter.adapters.config;

import com.clinicai.security.app.JwtTokenValidator;
import com.clinicai.security.adapters.config.SharedJwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtTokenValidator tokenValidator;
    
    public SecurityConfig(JwtTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }
    
    @Bean
    public SharedJwtAuthenticationFilter jwtAuthenticationFilter() {
        return new SharedJwtAuthenticationFilter(tokenValidator);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/encounters/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/encounters/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/encounters/clinician/**").hasAnyRole("CLINICIAN", "ADMIN")
                .requestMatchers("/api/encounters/patient/**").hasAnyRole("PATIENT", "CLINICIAN", "ADMIN")
                .requestMatchers("/api/encounters/receptionist/**").hasAnyRole("RECEPTIONIST", "ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}