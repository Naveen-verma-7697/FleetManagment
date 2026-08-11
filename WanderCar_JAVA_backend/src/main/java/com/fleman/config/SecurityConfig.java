package com.fleman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fleman.security.CustomAuthorizationRequestResolver;
import com.fleman.security.CustomOAuth2UserService;
import com.fleman.security.JwtAuthenticationFilter;
import com.fleman.security.OAuth2SuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
// Spring Security is included only for BCryptPasswordEncoder — real password
// hashing for Customer.passwordHash — not for endpoint-level authorization.
// Every endpoint is intentionally left open (permitAll) because the current
// frontend never attaches a token to its requests (see JwtUtil's note).
// Adding a JwtAuthFilter that reads Authorization: Bearer <token> and
// restricts /api/customers/**, /api/bookings/me etc. to the matching
// customerId is the natural next step, not implemented here to match
// today's actual frontend behaviour.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/////sso///
	 private final JwtAuthenticationFilter jwtFilter; 
	 private final OAuth2SuccessHandler successHandler;
	 private final CustomOAuth2UserService oauth2UserService;
	 private final ClientRegistrationRepository clientRegistrationRepository;
	    
	 public SecurityConfig(
		        JwtAuthenticationFilter jwtFilter,
		        OAuth2SuccessHandler successHandler,
		        CustomOAuth2UserService oauth2UserService,
		        ClientRegistrationRepository clientRegistrationRepository) {

		    this.jwtFilter = jwtFilter;
		    this.successHandler = successHandler;
		    this.oauth2UserService = oauth2UserService;
		    this.clientRegistrationRepository = clientRegistrationRepository;
		}
	    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
   

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(sm ->
                    sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            .oauth2Login(oauth -> oauth

            	    .authorizationEndpoint(auth ->
            	        auth.authorizationRequestResolver(
            	        		new CustomAuthorizationRequestResolver(
            	        		        clientRegistrationRepository
            	        		)
            	        )
            	    )

            	    .userInfoEndpoint(user ->
            	        user.userService(oauth2UserService)
            	    )

            	    .successHandler(successHandler)
            	)

            .authorizeHttpRequests(auth -> auth

                    // Login APIs
                    .requestMatchers("/api/auth/**").permitAll()

                    // Google OAuth
                    .requestMatchers("/oauth2/**").permitAll()
                    .requestMatchers("/login/**").permitAll()
                    
                    .requestMatchers("/profile").permitAll() //add this for profile

                    // .NET backend fetches invoice PDFs over HTTP (server-to-server,
                    // see InvoiceController / JavaMicroserviceClient.GetInvoicePdfAsync)
                    .requestMatchers("/api/invoices/**").permitAll()

                    // Guest booking
                    .requestMatchers(HttpMethod.POST, "/api/bookings").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/customers/guest").permitAll()

                    // Public lookup APIs
                    .requestMatchers(
                            "/api/states/**",
                            "/api/cities/**",
                            "/api/hubs/**",
                            "/api/airports/**",
                            "/api/car-types/**",
                            "/api/cars/**",
                            "/api/addons/**"
                    ).permitAll()

                    // Staff APIs
                    .requestMatchers("/api/staff/**")
                    .hasRole("STAFF")

                    // Everything else requires JWT
                    .anyRequest()
                    .authenticated()
            );

        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}