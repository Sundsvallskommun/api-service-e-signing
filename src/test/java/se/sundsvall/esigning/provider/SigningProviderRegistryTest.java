package se.sundsvall.esigning.provider;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.provider.configuration.ProviderProperties;
import se.sundsvall.esigning.provider.model.SigningInstanceInfo;
import se.sundsvall.esigning.provider.model.SigningResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class SigningProviderRegistryTest {

	private static final SigningProvider COMFACT = new TestProvider("comfact");
	private static final SigningProvider OTHER = new TestProvider("other");

	@Test
	void resolveByMunicipalityMapping() {
		final var registry = new SigningProviderRegistry(new ProviderProperties("other", Map.of("2281", "comfact")), List.of(COMFACT, OTHER));

		assertThat(registry.resolve("2281")).isSameAs(COMFACT);
	}

	@Test
	void resolveFallsBackToDefault() {
		final var registry = new SigningProviderRegistry(new ProviderProperties("other", Map.of("2281", "comfact")), List.of(COMFACT, OTHER));

		assertThat(registry.resolve("1984")).isSameAs(OTHER);
	}

	@Test
	void resolveFallsBackToDefaultWhenMappingIsNull() {
		final var registry = new SigningProviderRegistry(new ProviderProperties("comfact", null), List.of(COMFACT));

		assertThat(registry.resolve("2281")).isSameAs(COMFACT);
	}

	@Test
	void resolveThrowsWhenNoProviderConfigured() {
		final var registry = new SigningProviderRegistry(new ProviderProperties(null, Map.of()), List.of(COMFACT));

		assertThatThrownBy(() -> registry.resolve("2281"))
			.isInstanceOf(Problem.class)
			.hasMessage("Internal Server Error: No signing provider is configured for municipality 2281")
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}

	@Test
	void resolveThrowsWhenConfiguredProviderMissing() {
		final var registry = new SigningProviderRegistry(new ProviderProperties("missing", Map.of()), List.of(COMFACT));

		assertThatThrownBy(() -> registry.resolve("2281"))
			.isInstanceOf(Problem.class)
			.hasMessage("Internal Server Error: Configured signing provider 'missing' for municipality 2281 is not available")
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}

	private record TestProvider(String id)
		implements
		SigningProvider {

		@Override
		public String getId() {
			return id;
		}

		@Override
		public SigningResult startSigning(final String municipalityId, final StartSigningRequest request) {
			return null;
		}

		@Override
		public SigningInstanceInfo getSigningInstance(final String municipalityId, final String providerCaseId) {
			return null;
		}
	}
}
