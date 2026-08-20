package com.Shree.ecom_web.security;

import com.Shree.ecom_web.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;


    // These APIs do NOT need JWT
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/api/auth")
                || path.startsWith("/api/payment")
                || path.startsWith("/api/orders")
                || path.startsWith("/api/products")
                || path.startsWith("/api/product")
                || path.startsWith("/api/cart");
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("=================================");
        System.out.println("REQUEST = " + request.getMethod()
                + " " + request.getRequestURI());

        String authHeader =
                request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);


        // No JWT → continue normally
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);


        try {

            String username =
                    jwtService.extractUsername(token);

            System.out.println("JWT USERNAME = " + username);


            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);


                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);


                System.out.println(
                        "AUTHENTICATION SET = "
                                + username
                );
            }

        } catch (Exception e) {

            System.out.println("========== JWT ERROR ==========");
            e.printStackTrace();
            System.out.println("================================");
        }


        filterChain.doFilter(request, response);
    }
}