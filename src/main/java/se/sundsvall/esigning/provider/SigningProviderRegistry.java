package se.sundsvall.esigning.provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.esigning.provider.configuration.ProviderProperties;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Resolves the active {@link SigningProvider} for a municipality, based on {@link ProviderProperties}. All providers
 * present on the classpath are auto-registered by their {@link SigningProvider#getId()}.
 */
@Component
public class SigningProviderRegistry {

	private static final String NO_PROVIDER_CONFIGURED = "No signing provider is configured for municipality %s";
	private static final String PROVIDER_NOT_AVAILABLE = "Configured signing provider '%s' for municipality %s is not available";
	private static final String UNKNOWN_PROVIDER_ID = "Unknown signing provider '%s'";

	private final ProviderProperties providerProperties;
	private final Map<String, SigningProvider> providersById;

	public SigningProviderRegistry(final ProviderProperties providerProperties, final List<SigningProvider> providers) {
		this.providerProperties = providerProperties;
		this.providersById = providers.stream().collect(Collectors.toMap(SigningProvider::getId, Function.identity()));
	}

	public SigningProvider resolve(final String municipalityId) {
		final var providerId = Optional.ofNullable(providerProperties.byMunicipalityId())
			.map(mapping -> mapping.get(municipalityId))
			.orElseGet(providerProperties::defaultProvider);

		if (providerId == null) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, NO_PROVIDER_CONFIGURED.formatted(municipalityId));
		}

		return Optional.ofNullable(providersById.get(providerId))
			.orElseThrow(() -> Problem.valueOf(INTERNAL_SERVER_ERROR, PROVIDER_NOT_AVAILABLE.formatted(providerId, municipalityId)));
	}

	public SigningProvider getById(final String providerId) {
		return Optional.ofNullable(providersById.get(providerId))
			.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, UNKNOWN_PROVIDER_ID.formatted(providerId)));
	}
}
