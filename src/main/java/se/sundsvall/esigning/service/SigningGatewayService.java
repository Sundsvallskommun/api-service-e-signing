package se.sundsvall.esigning.service;

import org.springframework.stereotype.Service;
import se.sundsvall.esigning.api.model.SigningInstanceResponse;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.integration.postportalservice.PostportalserviceIntegration;
import se.sundsvall.esigning.integration.postportalservice.SigningEvent;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;

/**
 * Entry point for the provider-agnostic signing flow. Selects the configured provider for the municipality and
 * delegates the (synchronous) start of the signing process to it, and relays normalized provider events to the
 * consumer.
 */
@Service
public class SigningGatewayService {

	private final SigningProviderRegistry signingProviderRegistry;
	private final PostportalserviceIntegration postportalserviceIntegration;

	public SigningGatewayService(final SigningProviderRegistry signingProviderRegistry, final PostportalserviceIntegration postportalserviceIntegration) {
		this.signingProviderRegistry = signingProviderRegistry;
		this.postportalserviceIntegration = postportalserviceIntegration;
	}

	public StartSigningResponse startSigning(final String municipalityId, final StartSigningRequest request) {
		final SigningProvider provider = signingProviderRegistry.resolve(municipalityId);
		final var result = provider.startSigning(municipalityId, request);

		return StartSigningResponse.builder()
			.withProviderCaseId(result.providerCaseId())
			.withStatus(result.status().name())
			.withProvider(provider.getId())
			.withSignatoryUrls(result.signatoryUrls())
			.build();
	}

	public SigningInstanceResponse getSigningInstance(final String municipalityId, final String providerCaseId) {
		final var provider = signingProviderRegistry.resolve(municipalityId);
		final var info = provider.getSigningInstance(municipalityId, providerCaseId);

		return SigningInstanceResponse.builder()
			.withProviderCaseId(info.providerCaseId())
			.withStatus(info.status().name())
			.withProvider(provider.getId())
			.withExpires(info.expires())
			.withSignedDocument(info.signedDocument())
			.build();
	}

	public void cancelSigning(final String municipalityId, final String providerCaseId) {
		final var provider = signingProviderRegistry.resolve(municipalityId);
		provider.cancelSigning(municipalityId, providerCaseId);
	}

	/**
	 * Relays a normalized, provider-neutral signing event to Postportalservice. The provider-specific inbound resource
	 * has already mapped the provider's payload to {@link SigningEvent}; pps correlates the case by
	 * {@code customerReference} and the recipient by {@code signatory.partyId}.
	 */
	public void relaySigningEvent(final String municipalityId, final SigningEvent signingEvent) {
		postportalserviceIntegration.sendEvent(municipalityId, signingEvent);
	}
}
