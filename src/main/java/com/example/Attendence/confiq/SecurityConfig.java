package com.example.Attendence.confiq;

import com.example.Attendence.security.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues()))
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/login", "/logout").permitAll()
                       // .requestMatchers("/api/user/**").hasAuthority("ROLE_Admin") // Restrict access
                        .requestMatchers( "/api/user/**","/api/role/**").access((authenticationSupplier, context) -> {
                            Authentication authentication = authenticationSupplier.get(); // Unwrap Supplier

                            if (authentication != null && authentication.isAuthenticated()) {


                                // Get user authorities (roles)
                                var userRoles = authentication.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .toList(); // Convert to List for easy processing

                               // System.out.println("User Roles: " + userRoles);

                                // Extract the requested role dynamically from the user's authorities
                                for (String role : userRoles) {
                                   // System.out.println("Checking role: " + role);
                                    return new AuthorizationDecision(true);
                                }
                                System.out.println("No matching role found.");
                            }
                            return new AuthorizationDecision(false);
                        })
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            handleForbiddenRedirect(request, response);
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Ensure JWT filter runs before Spring Security

        return http.build();
    }

    private void handleForbiddenRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginPage = "/"; // Change this to your actual login page URL
        response.sendRedirect(loginPage);
    }
}
