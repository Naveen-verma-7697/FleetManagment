package com.fleman.security;

import com.fleman.entity.Customer;
import com.fleman.repository.CustomerRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final CustomerRepository customerRepository;

    public CustomOAuth2UserService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser =
                new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");
        Boolean verified =
                oauthUser.getAttribute("email_verified");

        Customer customer =
                customerRepository
                        .findByEmailIgnoreCase(email)
                        .orElse(null);

        if (customer == null) {

            customer = new Customer();

            customer.setEmail(email);
            customer.setFullName(name);

        }

        customer.setProvider(Customer.AuthProvider.GOOGLE);
        customer.setProviderId(googleId);
        customer.setEmailVerified(
                verified != null && verified
        );

        customerRepository.save(customer);

        return oauthUser;

    }
}