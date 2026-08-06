package com.fleman.security;

import com.fleman.entity.Customer;
import com.fleman.repository.CustomerRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final CustomerRepository customerRepository;

    public OAuth2SuccessHandler(
            JwtUtil jwtUtil,
            CustomerRepository customerRepository) {

        this.jwtUtil = jwtUtil;
        this.customerRepository = customerRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        Customer customer =
        	    customerRepository.findByEmailIgnoreCase(email)
        	        .orElseThrow(() ->
        	            new UsernameNotFoundException(email));

        String token = jwtUtil.generateToken(
                customer.getCustomerId(),
                customer.getEmail(),
                "CUSTOMER"
        );

        response.sendRedirect(
        	    "http://localhost:5173/oauth-success?token=" + token
        	);
    }
}