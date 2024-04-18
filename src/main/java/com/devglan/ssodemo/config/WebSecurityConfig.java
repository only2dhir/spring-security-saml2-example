package com.devglan.ssodemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.saml2.provider.service.registration.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private SamlProperties samlProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .saml2Login(saml2 -> saml2.loginProcessingUrl("/SSO/mysamlresponse")
                        .relyingPartyRegistrationRepository(relyingPartyRegistration()));
        return http.build();
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistration() {
        RelyingPartyRegistration registration =
                RelyingPartyRegistrations.fromMetadataLocation(samlProperties.getMetadataLocation())
                        .registrationId(samlProperties.getRelyingParty().getRegistrationId())
                        .entityId(samlProperties.getRelyingParty().getEntityId())
                        .assertionConsumerServiceLocation(samlProperties.getRelyingParty().getRedirectUrl())
                        .assertingPartyDetails(party -> party.entityId(samlProperties.getAssertingpParty().getEntityId())
                                .wantAuthnRequestsSigned(false)
                                .singleSignOnServiceLocation(samlProperties.getAssertingpParty().getServiceLocation())
                                .singleSignOnServiceBinding(Saml2MessageBinding.POST))
                        .build();
        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }
}
