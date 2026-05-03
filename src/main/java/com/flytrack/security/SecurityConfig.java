package com.flytrack.security;

import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) {
         http
                 .csrf(AbstractHttpConfigurer::disable)
                 .cors(Customizer.withDefaults())
                 .authorizeHttpRequests(auth -> auth
                         .requestMatchers("/api/v1/auth/**").permitAll()
                         .requestMatchers(HttpMethod.POST, "/api/v1/passengers").permitAll()
                         .requestMatchers(HttpMethod.GET, "/api/v1/baggage/tracking/**").authenticated()
                         .requestMatchers(HttpMethod.PATCH, "/api/v1/baggage/*/lost").authenticated()
                         .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                         .requestMatchers(HttpMethod.PATCH, "/api/v1/flights/*/estado", "/api/v1/baggage/*/estado").hasRole("ADMIN")
                         .requestMatchers(HttpMethod.POST, "/api/v1/flights/**", "/api/v1/baggage/**").hasAnyRole("ADMIN", "OPERADOR")
                         .requestMatchers(HttpMethod.PUT, "/api/v1/flights/**", "/api/v1/baggage/**").hasAnyRole("ADMIN", "OPERADOR")
                         .anyRequest().authenticated()
                 )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow localhost from any port (using allowedOriginPatterns)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "https://localhost:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
