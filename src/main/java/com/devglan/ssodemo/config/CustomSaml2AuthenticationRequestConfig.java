package com.devglan.ssodemo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.saml2.core.Saml2ParameterNames;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
public class CustomSaml2AuthenticationRequestConfig implements Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> {

    private static final String SAML2_REQUEST_COLLECTION = "saml2_requests";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public AbstractSaml2AuthenticationRequest loadAuthenticationRequest(HttpServletRequest request) {
        String relayState = request.getParameter(Saml2ParameterNames.RELAY_STATE);
        if (!StringUtils.hasText(relayState)) {
            log.error("Relay state is null. Aborting....");
            return null;
        }
        Query query = Query.query(Criteria.where("relayState").is(relayState));;
        AbstractSaml2AuthenticationRequest authenticationRequest = mongoTemplate.findOne(query, AbstractSaml2AuthenticationRequest.class, SAML2_REQUEST_COLLECTION);
        if (Objects.isNull(authenticationRequest)) {
            log.error("Couldn't find any saved authentication request for relay state {}", relayState);
            return null;
        }
        return authenticationRequest;
    }

    @Override
    public void saveAuthenticationRequest(AbstractSaml2AuthenticationRequest authenticationRequest, HttpServletRequest request, HttpServletResponse response) {
        mongoTemplate.save(authenticationRequest, SAML2_REQUEST_COLLECTION);
    }

    @Override
    public AbstractSaml2AuthenticationRequest removeAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) {
        AbstractSaml2AuthenticationRequest authRequest = loadAuthenticationRequest(request);
        if (Objects.nonNull(authRequest)) {
            mongoTemplate.remove(authRequest, SAML2_REQUEST_COLLECTION);
        }
        return authRequest;
    }
}
