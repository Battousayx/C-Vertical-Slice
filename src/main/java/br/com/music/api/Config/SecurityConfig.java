package br.com.music.api.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
                // .ignoringRequestMatchers(
                //     "/v1/**",
                //     "/api/v1/**",
                //     "/swagger-ui/**",
                //     "/v3/api-docs/**",
                //     "/swagger-resources/**",
                //     "/webjars/**"
                // )
            // )
            // .authorizeHttpRequests(authz -> authz
            //     // Permit Swagger and OpenAPI endpoints
            //     .requestMatchers(
            //         "/swagger-ui/**",
            //         "/v3/api-docs/**",
            //         "/swagger-resources/**",
            //         "/swagger-resources",
            //         "/webjars/**",
            //         "/api-docs",
            //         "/api-docs/**"
            //     ).permitAll()
            //     // Permit API endpoints
            //     .requestMatchers("/v1/**").permitAll()
            //     .requestMatchers("/api/v1/**").permitAll()
            //     // Require authentication for all other requests
            //     .anyRequest().authenticated()
            // )
            // .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
