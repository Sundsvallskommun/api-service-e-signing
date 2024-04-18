package se.sundsvall.esigning.integration.esigningprocess.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.esigning")
public record EsigningProcessProperties(int connectTimeout, int readTimeout) {
}
