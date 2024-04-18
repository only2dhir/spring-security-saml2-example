package com.devglan.ssodemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "devglan.saml")
public class SamlProperties {

    private String metadataLocation;
    private String successUrl;
    private RelyingParty relyingParty;
    private AssertingParty assertingpParty;

    @Data
    public static class RelyingParty {
        private String registrationId;
        private String entityId;
        private String baseUrl;
        private String redirectUrl;
    }

    @Data
    public static class AssertingParty {
        private String entityId;
        private String serviceLocation;
    }


}
