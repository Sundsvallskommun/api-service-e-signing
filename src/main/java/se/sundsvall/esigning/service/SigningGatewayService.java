package se.sundsvall.esigning.service;

import org.springframework.stereotype.Service;
import se.sundsvall.esigning.api.model.SigningInstanceResponse;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;

/**
 * Entry point for the provider-agnostic signing flow. Selects the configured provider for the municipality and
 * delegates the (synchronous) start of the signing process to it.
 */
@Service
public class SigningGatewayService {

	private final SigningProviderRegistry signingProviderRegistry;

	public SigningGatewayService(final SigningProviderRegistry signingProviderRegistry) {
		this.signingProviderRegistry = signingProviderRegistry;
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
}
