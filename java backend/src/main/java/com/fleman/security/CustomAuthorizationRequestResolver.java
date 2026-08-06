package com.fleman.security;



import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public class CustomAuthorizationRequestResolver
        implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(
            ClientRegistrationRepository repository) {

        defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        repository,
                        "/oauth2/authorization"
                );
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {

        OAuth2AuthorizationRequest req =
                defaultResolver.resolve(request);

        return customize(req);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(
            HttpServletRequest request,
            String clientRegistrationId) {

        OAuth2AuthorizationRequest req =
                defaultResolver.resolve(
                        request,
                        clientRegistrationId
                );

        return customize(req);
    }

    private OAuth2AuthorizationRequest customize(
            OAuth2AuthorizationRequest req) {

        if (req == null)
            return null;

        return OAuth2AuthorizationRequest
                .from(req)
                .additionalParameters(params -> {
                    params.put("prompt", "select_account");
                })
                .build();
    }
}