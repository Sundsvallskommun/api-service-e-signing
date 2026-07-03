package se.sundsvall.esigning.service;

import org.springframework.stereotype.Service;
import se.sundsvall.esigning.api.model.SigningEventNotification;
import se.sundsvall.esigning.api.model.SigningInstanceResponse;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.integration.postportalservice.PostportalserviceIntegration;
import se.sundsvall.esigning.integration.postportalservice.SigningCallbackRequest;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;

/**
 * Entry point for the provider-agnostic signing flow. Selects the configured provider for the municipality and
 * delegates the (synchronous) start of the signing process to it, and relays provider events back to the consumer.
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

	/**
	 * Handles an inbound provider event: pulls the authoritative signing-instance snapshot and relays the normalized
	 * status (and signed document, once available) to Postportalservice, which correlates the case by provider case id.
	 */
	public void handleProviderEvent(final String municipalityId, final String providerId, final SigningEventNotification notification) {
		final var provider = signingProviderRegistry.getById(providerId);
		final var info = provider.getSigningInstance(municipalityId, notification.getProviderCaseId());

		postportalserviceIntegration.sendCallback(municipalityId, new SigningCallbackRequest(info.providerCaseId(), info.status().name(), info.signedDocument()));
	}
}
