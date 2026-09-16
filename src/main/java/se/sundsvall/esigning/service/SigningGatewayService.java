package se.sundsvall.esigning.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.sundsvall.esigning.api.model.SigningInstanceResponse;
import se.sundsvall.esigning.api.model.StartSigningRequest;
import se.sundsvall.esigning.api.model.StartSigningResponse;
import se.sundsvall.esigning.integration.postportalservice.PostportalserviceIntegration;
import se.sundsvall.esigning.integration.postportalservice.SigningEvent;
import se.sundsvall.esigning.provider.SigningProvider;
import se.sundsvall.esigning.provider.SigningProviderRegistry;

import static java.util.Objects.isNull;
import static se.sundsvall.dept44.util.LogUtils.sanitizeForLogging;

/**
 * Entry point for the provider-agnostic signing flow. Selects the configured provider for the municipality and
 * delegates the (synchronous) start of the signing process to it, and relays normalized provider events to the
 * consumer.
 */
@Service
public class SigningGatewayService {

	private static final Logger LOG = LoggerFactory.getLogger(SigningGatewayService.class);

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
	 * <p>
	 * An event whose customer reference is not a Postportalen message id cannot be routed: pps takes the message id as
	 * a path variable behind {@code @ValidUuid} and answers 400, which travels back through the webhook chain as a 502
	 * and makes the provider redeliver the same event indefinitely - withdrawing the case does not stop it, only a 2xx
	 * does. pps already acknowledges message ids it does not recognize for exactly that reason, but a reference that is
	 * not a UUID never reaches that check. Acknowledge those events here instead. {@code customerReference} is
	 * validated on the way in (see {@link se.sundsvall.esigning.api.model.StartSigningRequest}), so this only catches
	 * cases created before that validation existed, or outside this service.
	 */
	public void relaySigningEvent(final String municipalityId, final SigningEvent signingEvent) {
		if (!isPostportalenMessageId(signingEvent.customerReference())) {
			LOG.warn("Acknowledging signing event for provider case {} without relaying it: customer reference '{}' is not a Postportalen message id",
				sanitizeForLogging(signingEvent.providerCaseId()), sanitizeForLogging(signingEvent.customerReference()));
			return;
		}
		postportalserviceIntegration.sendEvent(municipalityId, signingEvent);
	}

	/**
	 * Mirrors the {@code @ValidUuid} check pps applies to the message id path variable, so an event is only relayed
	 * when pps can actually accept it.
	 */
	private static boolean isPostportalenMessageId(final String customerReference) {
		if (isNull(customerReference)) {
			return false;
		}
		try {
			UUID.fromString(customerReference);
			return true;
		} catch (final IllegalArgumentException _) {
			return false;
		}
	}
}
