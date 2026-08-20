package com.Shree.ecom_web.security;

import com.Shree.ecom_web.service.MyUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private MyUserDetailsService userDetailsService;


    // ==========================================
    // PASSWORD ENCODER
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // ==========================================
    // AUTHENTICATION PROVIDER
    // ==========================================

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }


    // ==========================================
    // AUTHENTICATION MANAGER
    // ==========================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }


    // ==========================================
    // CORS CONFIGURATION
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        /*
         * TEMPORARY:
         * Allow requests from your Netlify frontend.
         *
         * We are using allowedOriginPatterns instead
         * of allowedOrigins so that "*" works correctly.
         */
        configuration.setAllowedOriginPatterns(
                Arrays.asList("*")
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        /*
         * Your frontend sends the JWT in the Authorization
         * header. We don't need browser credentials/cookies.
         */
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }


    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ==================================
                // CSRF
                // ==================================

                .csrf(csrf -> csrf.disable())


                // ==================================
                // ENABLE CORS
                // ==================================

                .cors(Customizer.withDefaults())


                // ==================================
                // AUTHORIZATION
                // ==================================

                .authorizeHttpRequests(auth -> auth

                        // CORS preflight requests
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()


                        // ==================================
                        // AUTHENTICATION
                        // ==================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // ==================================
                        // PRODUCTS
                        // ==================================

                        .requestMatchers(
                                "/api/products/**",
                                "/api/product/**"
                        ).permitAll()


                        // ==================================
                        // CART
                        // ==================================

                        .requestMatchers(
                                "/api/cart/**"
                        ).permitAll()


                        // ==================================
                        // PAYMENT
                        // ==================================

                        .requestMatchers(
                                "/api/payment/**"
                        ).permitAll()


                        // ==================================
                        // ORDERS
                        // ==================================

                        .requestMatchers(
                                "/api/orders/**"
                        ).permitAll()


                        // ==================================
                        // H2 CONSOLE
                        // ==================================

                        .requestMatchers(
                                "/h2-console/**"
                        ).permitAll()


                        // ==================================
                        // EVERYTHING ELSE
                        // ==================================

                        .anyRequest().permitAll()
                )


                // ==================================
                // STATELESS
                // ==================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ==================================
                // AUTHENTICATION PROVIDER
                // ==================================

                .authenticationProvider(
                        authenticationProvider()
                );


        // ==========================================
        // H2 CONSOLE
        // ==========================================

        http.headers(headers ->
                headers.frameOptions(frame ->
                        frame.disable()
                )
        );


        return http.build();
    }
}