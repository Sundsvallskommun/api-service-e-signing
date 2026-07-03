package se.sundsvall.esigning.integration.postportalservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.postportalservice")
public record PostportalserviceProperties(int connectTimeout, int readTimeout) {
}
