package se.sundsvall.esigning.provider.configuration;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of which signing provider a given municipality (organization) uses.
 *
 * @param defaultProvider  the provider id to use when no municipality-specific mapping exists
 * @param byMunicipalityId provider id keyed by municipality id, e.g. {@code {"2281": "comfact"}}
 */
@ConfigurationProperties("esigning.provider")
public record ProviderProperties(String defaultProvider, Map<String, String> byMunicipalityId) {
}
