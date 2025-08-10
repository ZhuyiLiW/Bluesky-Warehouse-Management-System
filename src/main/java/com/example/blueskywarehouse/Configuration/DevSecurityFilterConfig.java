package com.example.blueskywarehouse.Configuration;

import com.example.blueskywarehouse.Logging.LogContextFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile("dev")
public class DevSecurityFilterConfig {


    @Autowired
    private LogContextFilter logContextFilter;
    static Logger logger = LoggerFactory.getLogger(DevSecurityFilterConfig.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static void writeJson(HttpServletResponse response, int status, Object body) {
        try {
            if (response.isCommitted()) return;
            response.setStatus(status);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(body));
            response.getWriter().flush();
        } catch (IOException e) {

            logger.warn("Failed to write JSON response: {}", e.getMessage(), e);

        }
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {



        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/UserController/login").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authEx) ->
                                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        Map.of("code", 401, "message", "Unauthenticated or token invalid")))
                        .accessDeniedHandler((request, response, deniedEx) ->
                                writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                                        Map.of("code", 403, "message", "Access denied")))
                )
                .addFilterBefore(logContextFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout.permitAll());

        return http.build();
    }
    //setAllowedOrigin ist sicherer und wird in Produktionsumgebungen empfohlen, da nur eine ganz bestimmte Domain erlaubt wird.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://192.168.178.94:3000","http://192.168.2.132:3000","http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(36000L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
