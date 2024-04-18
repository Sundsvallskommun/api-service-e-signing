package se.sundsvall.esigning.integration.esigning.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.esigning")
public record EsigningProperties(int connectTimeout, int readTimeout) {
}
